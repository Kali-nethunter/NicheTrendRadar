from __future__ import annotations

import hashlib
import json
import os
import secrets
import sqlite3
from contextlib import closing
from pathlib import Path
from typing import List

from fastapi import FastAPI, Header, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

APP_NAME = "Niche Trend Radar API"
DB_PATH = Path(os.getenv("DATABASE_PATH", str(Path(__file__).with_name("niche_trend_radar.db"))))

app = FastAPI(title=APP_NAME, version="1.0.1", description="Backend API for the Niche Trend Radar Android app.")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

class Niche(BaseModel):
    id: int | None = None
    name: str
    keywords: List[str] = Field(default_factory=list)
    platforms: List[str] = Field(default_factory=list)

class Trend(BaseModel):
    trend_id: str
    title: str
    score: int
    growth_label: str
    source_summary: str

class ContentIdea(BaseModel):
    id: int | None = None
    title: str
    hook: str
    outline: List[str]
    cta: str
    platform: str | None = None

class IdeaResponse(BaseModel):
    ideas: List[ContentIdea]

class AuthRequest(BaseModel):
    email: str
    password: str

class AuthResponse(BaseModel):
    token: str
    user_id: int
    email: str

class HealthResponse(BaseModel):
    status: str
    service: str
    version: str

def get_db() -> sqlite3.Connection:
    connection = sqlite3.connect(DB_PATH)
    connection.row_factory = sqlite3.Row
    return connection

def hash_password(password: str, salt: bytes | None = None) -> str:
    salt = salt or secrets.token_bytes(16)
    digest = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 120_000)
    return salt.hex() + "$" + digest.hex()

def verify_password(password: str, stored: str) -> bool:
    try:
        salt_hex, digest_hex = stored.split("$", 1)
        digest = hashlib.pbkdf2_hmac("sha256", password.encode(), bytes.fromhex(salt_hex), 120_000)
        return secrets.compare_digest(digest.hex(), digest_hex)
    except (ValueError, TypeError):
        return False

def init_db() -> None:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    with closing(get_db()) as db:
        db.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """)
        db.execute("""
            CREATE TABLE IF NOT EXISTS sessions (
                token TEXT PRIMARY KEY,
                user_id INTEGER NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """)
        db.execute("""
            CREATE TABLE IF NOT EXISTS niches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                keywords TEXT NOT NULL,
                platforms TEXT NOT NULL,
                user_id INTEGER
            )
        """)
        db.execute("""
            CREATE TABLE IF NOT EXISTS radar_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                niche_id INTEGER NOT NULL,
                platform TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """)
        db.execute("""
            CREATE TABLE IF NOT EXISTS saved_ideas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                hook TEXT NOT NULL,
                outline TEXT NOT NULL,
                cta TEXT NOT NULL,
                platform TEXT
            )
        """)
        columns = {
            row["name"]
            for row in db.execute("PRAGMA table_info(saved_ideas)").fetchall()
        }
        if "platform" not in columns:
            db.execute("ALTER TABLE saved_ideas ADD COLUMN platform TEXT")
        niche_columns = {row["name"] for row in db.execute("PRAGMA table_info(niches)").fetchall()}
        if "user_id" not in niche_columns:
            db.execute("ALTER TABLE niches ADD COLUMN user_id INTEGER")
        idea_columns = {row["name"] for row in db.execute("PRAGMA table_info(saved_ideas)").fetchall()}
        if "user_id" not in idea_columns:
            db.execute("ALTER TABLE saved_ideas ADD COLUMN user_id INTEGER")
        db.commit()

@app.on_event("startup")
def startup() -> None:
    init_db()

@app.get("/", response_model=HealthResponse)
def root() -> HealthResponse:
    return HealthResponse(status="ok", service=APP_NAME, version="1.0.0")

@app.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    return HealthResponse(status="ok", service=APP_NAME, version="1.0.0")

def get_current_user(authorization: str | None) -> sqlite3.Row:
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Authentication required")
    token = authorization[7:].strip()
    with closing(get_db()) as db:
        row = db.execute(
            "SELECT users.* FROM sessions JOIN users ON users.id = sessions.user_id WHERE sessions.token = ?",
            (token,),
        ).fetchone()
    if row is None:
        raise HTTPException(status_code=401, detail="Invalid or expired session")
    return row

@app.post("/api/auth/signup", response_model=AuthResponse)
def signup(request: AuthRequest) -> AuthResponse:
    email = request.email.strip().lower()
    if "@" not in email or len(email) < 5:
        raise HTTPException(status_code=400, detail="Enter a valid email address")
    if len(request.password) < 8:
        raise HTTPException(status_code=400, detail="Password must be at least 8 characters")
    token = secrets.token_urlsafe(32)
    with closing(get_db()) as db:
        if db.execute("SELECT id FROM users WHERE email = ?", (email,)).fetchone():
            raise HTTPException(status_code=409, detail="An account with this email already exists")
        cursor = db.execute("INSERT INTO users (email, password_hash) VALUES (?, ?)", (email, hash_password(request.password)))
        user_id = cursor.lastrowid
        db.execute("INSERT INTO sessions (token, user_id) VALUES (?, ?)", (token, user_id))
        db.execute("UPDATE niches SET user_id = ? WHERE user_id IS NULL", (user_id,))
        db.execute("UPDATE saved_ideas SET user_id = ? WHERE user_id IS NULL", (user_id,))
        db.commit()
    return AuthResponse(token=token, user_id=user_id, email=email)

@app.post("/api/auth/login", response_model=AuthResponse)
def login(request: AuthRequest) -> AuthResponse:
    email = request.email.strip().lower()
    with closing(get_db()) as db:
        user = db.execute("SELECT * FROM users WHERE email = ?", (email,)).fetchone()
        if user is None or not verify_password(request.password, user["password_hash"]):
            raise HTTPException(status_code=401, detail="Incorrect email or password")
        token = secrets.token_urlsafe(32)
        db.execute("INSERT INTO sessions (token, user_id) VALUES (?, ?)", (token, user["id"]))
        db.commit()
    return AuthResponse(token=token, user_id=user["id"], email=email)

@app.post("/api/auth/logout")
def logout(authorization: str | None = Header(default=None)) -> dict:
    if authorization and authorization.startswith("Bearer "):
        with closing(get_db()) as db:
            db.execute("DELETE FROM sessions WHERE token = ?", (authorization[7:].strip(),))
            db.commit()
    return {"status": "logged_out"}

@app.get("/api/auth/me", response_model=AuthResponse)
def me(authorization: str | None = Header(default=None)) -> AuthResponse:
    user = get_current_user(authorization)
    return AuthResponse(token=authorization[7:].strip(), user_id=user["id"], email=user["email"])

@app.post("/api/niches")
def create_niche(niche: Niche, authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    name = niche.name.strip() or "General"
    keywords = [k.strip() for k in niche.keywords if k.strip()]
    platforms = [p.strip() for p in niche.platforms if p.strip()]

    with closing(get_db()) as db:
        cursor = db.execute(
            "INSERT INTO niches (name, keywords, platforms, user_id) VALUES (?, ?, ?, ?)",
            (name, json.dumps(keywords), json.dumps(platforms), user["id"]),
        )
        db.commit()
        niche_id = cursor.lastrowid

    return {"niche_id": niche_id}

@app.get("/api/niches", response_model=List[Niche])
def get_niches(authorization: str | None = Header(default=None)) -> List[Niche]:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        rows = db.execute("SELECT * FROM niches WHERE user_id = ? ORDER BY id DESC", (user["id"],)).fetchall()

    return [
        Niche(
            id=row["id"],
            name=row["name"],
            keywords=json.loads(row["keywords"]),
            platforms=json.loads(row["platforms"]),
        )
        for row in rows
    ]

def make_trends(niche: Niche, platform: str) -> List[Trend]:
    keywords = niche.keywords or [niche.name]
    platform_name = platform.strip() or "YouTube"

    templates = [
        ("How {keyword} Is Changing in 2026", "Rising interest around {keyword} is creating new content opportunities."),
        ("The New {keyword} Playbook", "Creators are packaging {keyword} into practical, repeatable formats."),
        ("{keyword}: 5 Things People Want to Know", "Educational content around {keyword} is suited to high-intent discovery."),
        ("Beginner's Guide to {keyword}", "Beginner-friendly explainers can capture users entering the topic."),
        ("{keyword} Tools, Trends & Mistakes", "Tool comparisons and mistake-based content can generate strong engagement."),
    ]

    trends: List[Trend] = []
    for index, keyword in enumerate(keywords[:5]):
        keyword = keyword or niche.name
        title_template, summary_template = templates[index % len(templates)]
        title = title_template.format(keyword=keyword)
        summary = summary_template.format(keyword=keyword)
        digest = hashlib.sha256(
            f"{niche.id}:{niche.name}:{keyword}:{platform_name}".encode()
        ).hexdigest()
        score = 65 + (int(digest[:2], 16) % 31)
        growth = "High growth" if score >= 85 else "Growing" if score >= 75 else "Emerging"

        trends.append(
            Trend(
                trend_id=digest[:12],
                title=title,
                score=score,
                growth_label=growth,
                source_summary=f"Demo trend signal for {platform_name}. {summary}",
            )
        )

    return sorted(trends, key=lambda item: item.score, reverse=True)

@app.get("/api/trends", response_model=List[Trend])
def get_trends(
    niche_id: int = Query(..., ge=1),
    platform: str = Query("YouTube", min_length=1),
    authorization: str | None = Header(default=None),
) -> List[Trend]:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        row = db.execute("SELECT * FROM niches WHERE id = ? AND user_id = ?", (niche_id, user["id"])).fetchone()

    if row is None:
        raise HTTPException(status_code=404, detail="Niche not found")

    with closing(get_db()) as db:
        db.execute("INSERT INTO radar_history (user_id, niche_id, platform) VALUES (?, ?, ?)", (user["id"], niche_id, platform.strip() or "YouTube"))
        db.commit()

    niche = Niche(
        id=row["id"],
        name=row["name"],
        keywords=json.loads(row["keywords"]),
        platforms=json.loads(row["platforms"]),
    )
    return make_trends(niche, platform)

@app.post("/api/ideas/generate", response_model=IdeaResponse)
def generate_ideas(request: dict) -> IdeaResponse:
    trend_topic = str(request.get("trend_topic", "")).strip()
    niche = str(request.get("niche", "")).strip() or "General"
    platform = str(request.get("platform", "")).strip() or "YouTube"

    if not trend_topic:
        raise HTTPException(status_code=400, detail="trend_topic is required")

    ideas = [
        ContentIdea(
            title=f"{trend_topic}: What Nobody Tells Beginners",
            platform=platform,
            hook=f"Most people start with {trend_topic} the wrong way. Here is the simpler approach.",
            outline=[
                f"Why {trend_topic} matters in {niche}",
                "The biggest beginner mistake",
                "A practical step-by-step framework",
                "Tools and examples to use next",
            ],
            cta=f"Follow for more {niche} ideas and trend breakdowns.",
        ),
        ContentIdea(
            title=f"5 Fast Content Ideas Around {trend_topic}",
            platform=platform,
            hook=f"Need content for {platform}? Here are five angles you can create this week.",
            outline=[
                "Educational explainer",
                "Myth vs fact",
                "Case study",
                "Tool or workflow comparison",
                "Quick actionable checklist",
            ],
            cta="Save this list and turn one idea into your next post.",
        ),
        ContentIdea(
            title=f"{trend_topic} Explained in 60 Seconds",
            platform=platform,
            hook=f"If you have one minute, you can understand the core idea behind {trend_topic}.",
            outline=[
                "One-line definition",
                "Why people care now",
                "One real example",
                "One action viewers can take today",
            ],
            cta="Comment with the next topic you want explained.",
        ),
    ]
    return IdeaResponse(ideas=ideas)

@app.post("/api/ideas/save")
def save_idea(idea: ContentIdea, authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        cursor = db.execute(
            "INSERT INTO saved_ideas (title, hook, outline, cta, platform, user_id) VALUES (?, ?, ?, ?, ?, ?)",
            (idea.title, idea.hook, json.dumps(idea.outline), idea.cta, idea.platform, user["id"]),
        )
        db.commit()
    return {"status": "saved", "idea_id": str(cursor.lastrowid)}

@app.get("/api/ideas/saved", response_model=List[ContentIdea])
def get_saved_ideas(authorization: str | None = Header(default=None)) -> List[ContentIdea]:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        rows = db.execute("SELECT * FROM saved_ideas WHERE user_id = ? ORDER BY id DESC", (user["id"],)).fetchall()

    return [
        ContentIdea(
            id=row["id"],
            title=row["title"],
            hook=row["hook"],
            outline=json.loads(row["outline"]),
            cta=row["cta"],
            platform=row["platform"],
        )
        for row in rows
    ]

@app.delete("/api/ideas/{idea_id}")
def delete_idea(idea_id: int, authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        cursor = db.execute("DELETE FROM saved_ideas WHERE id = ? AND user_id = ?", (idea_id, user["id"]))
        db.commit()

    if cursor.rowcount == 0:
        raise HTTPException(status_code=404, detail="Saved idea not found")

    return {"status": "deleted", "idea_id": str(idea_id)}


class ChangePasswordRequest(BaseModel):
    current_password: str
    new_password: str

@app.post("/api/auth/change-password")
def change_password(request: ChangePasswordRequest, authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    if len(request.new_password) < 8:
        raise HTTPException(status_code=400, detail="New password must be at least 8 characters")
    if not verify_password(request.current_password, user["password_hash"]):
        raise HTTPException(status_code=401, detail="Current password is incorrect")
    with closing(get_db()) as db:
        db.execute("UPDATE users SET password_hash = ? WHERE id = ?", (hash_password(request.new_password), user["id"]))
        db.commit()
    return {"status": "password_changed"}

@app.post("/api/auth/logout-all")
def logout_all(authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    current_token = authorization[7:].strip()
    with closing(get_db()) as db:
        db.execute("DELETE FROM sessions WHERE user_id = ? AND token != ?", (user["id"], current_token))
        db.commit()
    return {"status": "other_sessions_signed_out"}

@app.get("/api/account/export")
def export_account(authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        niches = db.execute("SELECT id, name, keywords, platforms, user_id FROM niches WHERE user_id = ? ORDER BY id DESC", (user["id"],)).fetchall()
        ideas = db.execute("SELECT id, title, hook, outline, cta, platform FROM saved_ideas WHERE user_id = ? ORDER BY id DESC", (user["id"],)).fetchall()
        history = db.execute("SELECT id, niche_id, platform, created_at FROM radar_history WHERE user_id = ? ORDER BY id DESC", (user["id"],)).fetchall()
    return {
        "account": {"user_id": user["id"], "email": user["email"], "created_at": user["created_at"]},
        "niches": [{"id": r["id"], "name": r["name"], "keywords": json.loads(r["keywords"]), "platforms": json.loads(r["platforms"])} for r in niches],
        "saved_ideas": [{"id": r["id"], "title": r["title"], "hook": r["hook"], "outline": json.loads(r["outline"]), "cta": r["cta"], "platform": r["platform"]} for r in ideas],
        "radar_history": [{"id": r["id"], "niche_id": r["niche_id"], "platform": r["platform"], "created_at": r["created_at"]} for r in history],
    }

@app.delete("/api/ideas/clear")
def clear_saved_ideas(authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        cursor = db.execute("DELETE FROM saved_ideas WHERE user_id = ?", (user["id"],))
        db.commit()
    return {"status": "cleared", "deleted": cursor.rowcount}

@app.delete("/api/radar/history")
def clear_radar_history(authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        cursor = db.execute("DELETE FROM radar_history WHERE user_id = ?", (user["id"],))
        db.commit()
    return {"status": "cleared", "deleted": cursor.rowcount}

@app.delete("/api/auth/account")
def delete_account(authorization: str | None = Header(default=None)) -> dict:
    user = get_current_user(authorization)
    with closing(get_db()) as db:
        db.execute("DELETE FROM sessions WHERE user_id = ?", (user["id"],))
        db.execute("DELETE FROM saved_ideas WHERE user_id = ?", (user["id"],))
        db.execute("DELETE FROM niches WHERE user_id = ?", (user["id"],))
        db.execute("DELETE FROM radar_history WHERE user_id = ?", (user["id"],))
        db.execute("DELETE FROM users WHERE id = ?", (user["id"],))
        db.commit()
    return {"status": "account_deleted"}

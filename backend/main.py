from __future__ import annotations

import hashlib
import json
import os
import sqlite3
from contextlib import closing
from pathlib import Path
from typing import List

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

APP_NAME = "Niche Trend Radar API"
DB_PATH = Path(os.getenv("DATABASE_PATH", str(Path(__file__).with_name("niche_trend_radar.db"))))

app = FastAPI(title=APP_NAME, version="1.0.0", description="Backend API for the Niche Trend Radar Android app.")

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

class HealthResponse(BaseModel):
    status: str
    service: str
    version: str

def get_db() -> sqlite3.Connection:
    connection = sqlite3.connect(DB_PATH)
    connection.row_factory = sqlite3.Row
    return connection

def init_db() -> None:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    with closing(get_db()) as db:
        db.execute("""
            CREATE TABLE IF NOT EXISTS niches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                keywords TEXT NOT NULL,
                platforms TEXT NOT NULL
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

@app.post("/api/niches")
def create_niche(niche: Niche) -> dict:
    name = niche.name.strip() or "General"
    keywords = [k.strip() for k in niche.keywords if k.strip()]
    platforms = [p.strip() for p in niche.platforms if p.strip()]

    with closing(get_db()) as db:
        cursor = db.execute(
            "INSERT INTO niches (name, keywords, platforms) VALUES (?, ?, ?)",
            (name, json.dumps(keywords), json.dumps(platforms)),
        )
        db.commit()
        niche_id = cursor.lastrowid

    return {"niche_id": niche_id}

@app.get("/api/niches", response_model=List[Niche])
def get_niches() -> List[Niche]:
    with closing(get_db()) as db:
        rows = db.execute("SELECT * FROM niches ORDER BY id DESC").fetchall()

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
) -> List[Trend]:
    with closing(get_db()) as db:
        row = db.execute("SELECT * FROM niches WHERE id = ?", (niche_id,)).fetchone()

    if row is None:
        raise HTTPException(status_code=404, detail="Niche not found")

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
def save_idea(idea: ContentIdea) -> dict:
    with closing(get_db()) as db:
        cursor = db.execute(
            "INSERT INTO saved_ideas (title, hook, outline, cta, platform) VALUES (?, ?, ?, ?, ?)",
            (idea.title, idea.hook, json.dumps(idea.outline), idea.cta, idea.platform),
        )
        db.commit()
    return {"status": "saved", "idea_id": str(cursor.lastrowid)}

@app.get("/api/ideas/saved", response_model=List[ContentIdea])
def get_saved_ideas() -> List[ContentIdea]:
    with closing(get_db()) as db:
        rows = db.execute("SELECT * FROM saved_ideas ORDER BY id DESC").fetchall()

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
def delete_idea(idea_id: int) -> dict:
    with closing(get_db()) as db:
        cursor = db.execute("DELETE FROM saved_ideas WHERE id = ?", (idea_id,))
        db.commit()

    if cursor.rowcount == 0:
        raise HTTPException(status_code=404, detail="Saved idea not found")

    return {"status": "deleted", "idea_id": str(idea_id)}

# Niche Trend Radar Backend

FastAPI backend for the existing Android app.

## API contract

- GET /
- GET /health
- POST /api/niches
- GET /api/niches
- GET /api/trends?niche_id=1&platform=YouTube
- POST /api/ideas/generate
- POST /api/ideas/save
- GET /api/ideas/saved
- DELETE /api/ideas/{idea_id}

## Local run

From the repository root:

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000
```

FastAPI docs:

http://localhost:8000/docs

The existing Android emulator configuration uses:

http://10.0.2.2:8000/

## Important

The trend generator in this first backend phase is deterministic demo data generated from the submitted niche and keywords. It makes the existing APK testable end-to-end.

It is NOT yet a live external trend-data integration. A later phase can connect real trend/search/social data providers while keeping the same Android API contract.

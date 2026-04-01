# PMO Monorepo

Implementation of the project management system design with separated UI, backend, and database concerns under `src/`.

## Structure

- `src/ui` - React + TypeScript + Vite
- `src/backend` - Spring Boot REST API
- `src/database` - local SQL bootstrap assets
- `src/scripts` - local run/test orchestration scripts
- `infra/docker-compose.yml` - local stack startup

## Quick start

1. Copy environment templates:
   - `cp infra/env/.env.example .env`
2. Start stack:
   - `bash src/scripts/dev-up.sh`
3. Open:
   - UI: `http://localhost:5173`
   - Backend health: `http://localhost:8080/api/v1/health`

## Run tests

- `bash src/scripts/test-all.sh`

This runs backend unit + integration tests, frontend unit tests, then Playwright e2e.

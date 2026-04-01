#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

compose_cmd() {
  if docker compose version >/dev/null 2>&1; then
    docker compose "$@"
  else
    docker-compose "$@"
  fi
}

if [[ ! -f ".env" ]]; then
  cp infra/env/.env.example .env
fi

compose_cmd -f infra/docker-compose.yml --env-file .env up -d --build
bash src/scripts/wait-for-services.sh

echo "UI: http://localhost:${UI_PORT:-5173}"
echo "Backend: http://localhost:${BACKEND_PORT:-8080}/api/v1/health"

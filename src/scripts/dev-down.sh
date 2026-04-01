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

if [[ -f ".env" ]]; then
  compose_cmd -f infra/docker-compose.yml --env-file .env down
else
  compose_cmd -f infra/docker-compose.yml down
fi

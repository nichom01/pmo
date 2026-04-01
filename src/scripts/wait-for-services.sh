#!/usr/bin/env bash
set -euo pipefail

wait_for_url() {
  local name="$1"
  local url="$2"
  local attempts=60

  for ((i=1; i<=attempts; i++)); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "$name is ready at $url"
      return 0
    fi
    sleep 1
  done

  echo "Timed out waiting for $name at $url" >&2
  return 1
}

wait_for_url "backend" "http://localhost:${BACKEND_PORT:-8080}/api/v1/health"
wait_for_url "ui" "http://localhost:${UI_PORT:-5173}"

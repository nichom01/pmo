#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "Running backend tests..."
if [[ -f "$ROOT_DIR/src/backend/gradlew" ]]; then
  (cd "$ROOT_DIR/src/backend" && ./gradlew test)
elif command -v gradle >/dev/null 2>&1; then
  (cd "$ROOT_DIR/src/backend" && gradle test)
else
  docker run --rm \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v "$ROOT_DIR/src/backend:/app" \
    -w /app \
    gradle:8.10-jdk21 \
    gradle test --no-daemon
fi

echo "Running frontend unit tests..."
(cd "$ROOT_DIR/src/ui" && npm install && npm run test:unit)

echo "Ensuring local stack is running for e2e..."
bash "$ROOT_DIR/src/scripts/dev-up.sh"

echo "Running e2e tests..."
(cd "$ROOT_DIR/src/ui" && npx playwright install --with-deps chromium && npm run test:e2e)

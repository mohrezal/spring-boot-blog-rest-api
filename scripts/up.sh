#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

./scripts/generate-s3-config.sh

docker compose up --build -d
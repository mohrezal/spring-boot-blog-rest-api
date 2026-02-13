#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

set -a
source .env
set +a

mkdir -p configs
umask 077

cat > configs/s3.json <<JSON
{
  "identities": [
    {
      "name": "blog-api",
      "credentials": [
        {
          "accessKey": "${APPLICATION_STORAGE_ACCESS_KEY}",
          "secretKey": "${APPLICATION_STORAGE_SECRET_KEY}"
        }
      ],
      "actions": ["Read", "Write", "List", "Tagging"]
    }
  ]
}
JSON

echo "Generated configs/s3.json"
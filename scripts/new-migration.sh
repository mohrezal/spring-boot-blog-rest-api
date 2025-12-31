#!/bin/bash

if [ -z "$1" ]; then
    echo "USAGE: ./new-migration.sh <migration_name>"
    exit 1
fi

TIMESTAMP=$(date +%s)
MIGRATION_NAME=$1
FILENAME="${TIMESTAMP}_${MIGRATION_NAME}.sql"
MIGRATION_DIR="src/main/resources/db/changelog/migrations"

mkdir -p "${MIGRATION_DIR}"

AUTHOR=$(whoami)

cat > "${MIGRATION_DIR}/${FILENAME}" << EOF
--liquibase formatted sql
--changeset ${AUTHOR}:CHANGESET_ID_HERE
-- Your SQL here

--rollback -- Your rollback SQL here
EOF

echo "✅ Created: ${MIGRATION_DIR}/${FILENAME}"
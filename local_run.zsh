#!/usr/bin/env zsh
set -euxo pipefail

DB_PORT=${DB_PORT:-5432}

OUTPUT=$(mktemp)
docker run --rm --name jsonvalidatorpostgres \
  -e POSTGRES_PASSWORD=1 \
  -e POSTGRES_USER=app \
  -e POSTGRES_DB=db \
  -p "${DB_PORT}:5432" \
  postgres >> $OUTPUT 2&>1 &
echo "Docker spin up started."

echo "Starting application"
sbt run

docker kill jsonvalidatorpostgres
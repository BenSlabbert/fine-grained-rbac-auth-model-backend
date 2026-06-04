#!/bin/sh
set -eu

if [ -z "${DATABASES_TO_CREATE:-}" ]; then
  echo "No databases requested. Set DATABASES_TO_CREATE to a comma-separated list."
  exit 0
fi

echo "Creating databases: ${DATABASES_TO_CREATE}"

for raw_db in $(echo "${DATABASES_TO_CREATE}" | tr ',' ' '); do
  db="$(echo "${raw_db}" | xargs)"

  if [ -z "${db}" ]; then
    continue
  fi

  exists="$(psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname postgres -tAc "SELECT 1 FROM pg_database WHERE datname = '${db}'")"
  if [ "${exists}" = "1" ]; then
    echo "Database '${db}' already exists, skipping."
  else
    psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname postgres -c "CREATE DATABASE \"${db}\";"
    echo "Database '${db}' created."
  fi
done


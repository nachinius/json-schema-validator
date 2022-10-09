#!/usr/bin/env just --justfile

HOST := "http://127.0.0.1:8080"
CURL := "curl "
DB_PORT := "5432"

docker:
  docker run --rm --name jsonvalidatorpostgres \
    -e POSTGRES_PASSWORD=1 \
    -e POSTGRES_USER=app \
    -e POSTGRES_DB=db \
    -p {{DB_PORT}}:5432 \
    postgres

psql:
  PGPASSWORD=1 psql -h localhost -p {{DB_PORT}} -d db -U app

dockerstop:
  docker stop jsonvalidatorpostgres

start:
  #!/usr/bin/env zsh
  set -euxo pipefail
  export JDBC_URL="jdbc:postgresql://localhost:5432/db"
  export JDBC_USER="app"
  export JDBC_PASS="1"
  sbt run

hello:
  #!/usr/bin/env zsh
  set -euxo pipefail
  echo "hello world"
  {{CURL}} "{{HOST}}/hello?name=nachinius"

getschema id:
  #!/usr/bin/env zsh
  set -euxo pipefail
  echo "get /schema/{{id}}"
  {{CURL}} "{{HOST}}/schema/{{id}}"

postschema:
  #!/usr/bin/env zsh
  set -euxo pipefail
  echo "post /schema/h"
  payload=$(cat <<-JSONPAYLOAD
  { "jsonfield" : "value" }
  JSONPAYLOAD
  )
  {{CURL}} -X POST {{HOST}}/schema/h -d "$payload"

invalidupload:
  #!/usr/bin/env zsh
  set -euxo pipefail
  echo "invalid upload post /schema/hhh"
  payload=$(cat <<-JSONPAYLOAD
  { fake json "jsonfield" : "value" }
  JSONPAYLOAD
  )
  {{CURL}} -X POST {{HOST}}/schema/hhh -d "$payload"

getid: (getschema "myschemaid")

swagger:
  #!/usr/bin/env zsh
  open "{{HOST}}/docs"

####
e2e:
  #!/usr/bin/env zsh
  echo "\n========================================================\n"
  echo "upload\n"
  {{CURL}} -X POST {{HOST}}/schema/config-schema -d @config-schema.json
  echo "\n========================================================\n"
  echo "retrieve\n"
  {{CURL}} -X GET {{HOST}}/schema/config-schema
  echo "\n========================================================\n"
  echo "validate\n"
  {{CURL}} -X POST {{HOST}}/validate/config-schema -d @config.json
  echo "========================================================\n"
  echo "schema doesn't exist, can't validate:\n"
  {{CURL}} -X POST {{HOST}}/validate/2config-schema -d @bad-config.json
  echo "\n========================================================\n"
  echo "validation didn't pass:\n"
  {{CURL}} -X POST {{HOST}}/validate/config-schema -d @bad-config.json
  echo "\n========================================================\n"
  echo "config with nulls"
  {{CURL}} -X POST {{HOST}}/validate/config-schema -d @config-with-nulls.json



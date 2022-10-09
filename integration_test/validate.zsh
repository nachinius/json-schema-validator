#!/usr/bin/env zsh
#set -euxo pipefail

((FIRST_UPLOAD_RESPONSE=$(mktemp)
curl -s -X POST http://localhost:8080/schema/config-schema -d @config-schema.json --output $FIRST_UPLOAD_RESPONSE
diff $FIRST_UPLOAD_RESPONSE config-schema.upload.success.output
[[ $? -eq 0 ]] || (echo "fail upload json schema";exit 1)) &&

(SCHEMA_FETCH_RESPONSE=$(mktemp)
curl -s -X GET http://localhost:8080/schema/config-schema --output $SCHEMA_FETCH_RESPONSE
diff <(jq --sort-keys . $SCHEMA_FETCH_RESPONSE) <(jq --sort-keys . config-schema.json)
[[ $? -eq 0 ]] || (echo "fail retrieve schema";exit 1)) &&


(VALIDATE_DOCUMENT=$(mktemp)
curl -s -X POST http://localhost:8080/validate/config-schema -d @config.json --output $VALIDATE_DOCUMENT
diff $VALIDATE_DOCUMENT config-schema.validated.output
[[ $? -eq 0 ]] || (echo "fail validation document";exit 1)) &&


(VALIDATE_SCHEMA_MISSING=$(mktemp)
curl -s -X POST http://localhost:8080/validate/2config-schema -d @bad-config.json --output $VALIDATE_SCHEMA_MISSING
diff $VALIDATE_SCHEMA_MISSING config-schema.validated.missing.output
[[ $? -eq 0 ]] || (echo "fail validation when schema is missing";exit 1)) &&


(VALIDATE_BAD_DOCUMENT=$(mktemp)
curl -s -X POST http://localhost:8080/validate/config-schema -d @bad-config.json --output $VALIDATE_BAD_DOCUMENT
diff $VALIDATE_BAD_DOCUMENT config-schema.validate.bad.json.output
[[ $? -eq 0 ]] || (echo "fail validation when document is not json";exit 1)) &&

(VALIDATE_DOCUMENT_WITH_NULLS=$(mktemp)
curl -s -X POST http://localhost:8080/validate/config-schema -d @config-with-nulls.json --output $VALIDATE_DOCUMENT_WITH_NULLS
[[ $? -eq 0 ]] || (echo "fail validating document with nulls";exit 1)))

[[ $? -eq 0 ]] && echo "success" || echo "fail"
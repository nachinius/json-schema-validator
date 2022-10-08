#!/usr/bin/env just --justfile

HOST := "http://127.0.0.1:8080"
CURL := "curl "

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


getid: (getschema "myschemaid")

swagger:
  #!/usr/bin/env zsh
  open "{{HOST}}/docs"
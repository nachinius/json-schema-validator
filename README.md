# Json Validator Service #

## Running Locally ##

### Requirements
- sbt
- docker
- port 5432 available (for db)
- port 8080 available (for app)
- zsh (potentially should work on bash too)

```shell
./local_run.zsh
```

The command spins up a database and the app that listens for HTTP requests at `http://127.0.0.1:8080`

### Quick Integration Test

Once the application is running locally with **an empty database** (stop and restartthe `local_run.zsh` script)

```shell
cd integration_test
./validate.zsh
```

## License ##

This code is licensed under the Mozilla Public License Version 2.0, see the
[LICENSE](LICENSE) file for details.

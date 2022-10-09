# Json Validator Service #

## Running Locally ##

### Requirements
- sbt
- docker
- port 5432 available
- zsh (potentially should work on bash too)

```shell
./local_run.zsh
```

### Quick Integration Test

Once the application is running locally with **an empty database** (stop and restartthe `local_run.zsh` script)

```shell
cd integration_test
./validate.zsh
```

## License ##

This code is licensed under the Mozilla Public License Version 2.0, see the
[LICENSE](LICENSE) file for details.
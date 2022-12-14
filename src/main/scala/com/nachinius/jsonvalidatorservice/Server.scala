/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice

import cats.effect._
import cats.syntax.all._
import com.nachinius.jsonvalidatorservice.api._
import com.nachinius.jsonvalidatorservice.config._
import com.nachinius.jsonvalidatorservice.db.FlywayDatabaseMigrator
import com.nachinius.jsonvalidatorservice.implementations.DocumentValidatorService
import com.nachinius.jsonvalidatorservice.implementations.JsonSchemaValidatorWrapper
import com.typesafe.config._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import pureconfig._
import sttp.apispec.openapi.circe.yaml._
import sttp.tapir.docs.openapi._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

object Server extends IOApp {

  import doobie.util.transactor.Transactor

  val log = LoggerFactory.getLogger(Server.getClass())

  override def run(args: List[String]): IO[ExitCode] = {
    import com.nachinius.jsonvalidatorservice.implementations.PostgresRepository
    val migrator = new FlywayDatabaseMigrator

    for {
      config <- IO(ConfigFactory.load(getClass().getClassLoader()))
      dbConfig <- IO(
        ConfigSource.fromConfig(config).at(DatabaseConfig.CONFIG_KEY.toString).loadOrThrow[DatabaseConfig]
      )
      serviceConfig <- IO(
        ConfigSource.fromConfig(config).at(ServiceConfig.CONFIG_KEY.toString).loadOrThrow[ServiceConfig]
      )
      _ <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      xa = Transactor.fromDriverManager[IO](
        dbConfig.driver.value,
        dbConfig.url.value,
        dbConfig.user.value,
        dbConfig.pass.value
      )
//      inMemoryRepo <- InMemoryRepository.make[IO]
      postgresRepo <- PostgresRepository.make[IO](xa)
      wrapper              = new JsonSchemaValidatorWrapper()
      validatorService     = new DocumentValidatorService[IO](postgresRepo, wrapper)
      jsonSchemaCrudRoutes = new JsonSchemaCrudRoutes[IO](postgresRepo)
      validatorRoutes      = new DocumentValidatorRoutes[IO](validatorService)
      docs = OpenAPIDocsInterpreter().toOpenAPI(
        List(
          JsonSchemaCrudRoutes.fetch,
          JsonSchemaCrudRoutes.insert,
          DocumentValidatorRoutes.base
        ),
        "Json Document Validator",
        "1.0.0"
      )
      swaggerRoutes = Http4sServerInterpreter[IO]().toRoutes(SwaggerUI[IO](docs.toYaml))
      routes        = validatorRoutes.routes <+> jsonSchemaCrudRoutes.routes <+> swaggerRoutes
      httpApp       = Router("/" -> routes).orNotFound
      resource = EmberServerBuilder
        .default[IO]
        .withHost(serviceConfig.host)
        .withPort(serviceConfig.port)
        .withHttpApp(httpApp)
        .build
      fiber <- resource.use(server =>
        IO.delay(log.info("Server started at {}", server.address)) >> IO.never.as(ExitCode.Success)
      )
    } yield fiber
  }

}

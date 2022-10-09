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
import com.typesafe.config._
import com.nachinius.jsonvalidatorservice.api._
import com.nachinius.jsonvalidatorservice.config._
import com.nachinius.jsonvalidatorservice.db.FlywayDatabaseMigrator
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
  val log = LoggerFactory.getLogger(Server.getClass())

  override def run(args: List[String]): IO[ExitCode] = {
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
      helloWorldRoutes     = new HelloWorld[IO]
      jsonSchemaCrudRoutes = new JsonSchemaCrud[IO]
      docs = OpenAPIDocsInterpreter().toOpenAPI(
        List(HelloWorld.greetings, JsonSchemaCrud.fetch, JsonSchemaCrud.insert),
        "My Service",
        "1.0.0"
      )
      swaggerRoutes = Http4sServerInterpreter[IO]().toRoutes(SwaggerUI[IO](docs.toYaml))
      routes        = jsonSchemaCrudRoutes.routes <+> helloWorldRoutes.routes <+> swaggerRoutes
      httpApp       = Router("/" -> routes).orNotFound
      _             = println("c")
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

/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import cats.effect.Async
import com.nachinius.jsonvalidatorservice.model.JsonSchemaRepositoryAlgebra
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.endpoint
import sttp.tapir.path
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.statusCode
import cats.implicits.catsSyntaxEitherId

final class JsonSchemaCrud[F[_]: Async]() extends Http4sDsl[F] {

  import sttp.model.StatusCode

  private val crud: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(
      List(
        JsonSchemaCrud.fetch.serverLogic[F](_ => Async[F].delay(types.Response("", "", "", "").asRight[Unit])),
        JsonSchemaCrud.insert.serverLogic[F](_ => Async[F].delay(types.Response("", "", "", "").asRight[StatusCode]))
      )
    )

  val routes: HttpRoutes[F] = crud
}

object JsonSchemaCrud {

  import sttp.model.StatusCode
  import sttp.tapir.Endpoint
  import sttp.tapir.json.circe._

  private val base = endpoint
    .in("schema")
    .in(path[String])
    .out(jsonBody[types.Response])

  val fetch: Endpoint[Unit, String, Unit, types.Response, Any] =
    base.get.description("Download a JSON Schema with unique `SCHEMAID`")
  val insert: Endpoint[Unit, String, StatusCode, types.Response, Any] =
    base.post.description("Upload a JSON Schema with unique `SCHEMAID`").errorOut(statusCode)
}

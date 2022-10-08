/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import cats.effect.Async
import cats.implicits.catsSyntaxEitherId
import cats.implicits.catsSyntaxOptionId
import com.nachinius.jsonvalidatorservice.api.api._
import com.nachinius.jsonvalidatorservice.model.JsonDocument
import com.nachinius.jsonvalidatorservice.model.SchemaId
import io.circe._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.codec.monix.newtype._
import sttp.tapir.endpoint
import sttp.tapir.json.circe._
import sttp.tapir.path
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.statusCode

final class JsonSchemaCrud[F[_]: Async]() extends Http4sDsl[F] {

  private val crud: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(
      List(
        JsonSchemaCrud.fetch.serverLogic[F](name =>
          Async[F].delay(JsonDocument(Json.fromJsonObject(JsonObject(("k1", Json.fromString("v1"))))).asRight[Unit])
        ),
        JsonSchemaCrud.insert.serverLogic[F] { case (name, json) =>
          Async[F].delay(
            types.Response("uploadSchema", name.value, "success", json.value.noSpacesSortKeys.some).asRight[StatusCode]
          )
        }
      )
    )

  val routes: HttpRoutes[F] = crud
}

object JsonSchemaCrud {

  private val base = endpoint
    .in("schema")
    .in(path[SchemaId].description("SCHEMAID"))

  val fetch: Endpoint[Unit, SchemaId, Unit, JsonDocument, Any] =
    base.get.description("Download a JSON Schema with unique `SCHEMAID`").out(jsonBody[JsonDocument])
  val insert: Endpoint[Unit, (SchemaId, JsonDocument), StatusCode, types.Response, Any] =
    base.post
      .in(jsonBody[JsonDocument])
      .description("Upload a JSON Schema with unique `SCHEMAID`")
      .out(jsonBody[types.Response])
      .errorOut(statusCode)
}

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
import cats.implicits.toFunctorOps
import contrib.codecs._
import com.nachinius.jsonvalidatorservice.model.JsonDocument
import com.nachinius.jsonvalidatorservice.model.JsonSchemaRepositoryAlgebra
import com.nachinius.jsonvalidatorservice.model.SchemaAlreadyExists
import com.nachinius.jsonvalidatorservice.model.SchemaId
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.model.StatusCode
import sttp.tapir.codec.monix.newtype._
import sttp.tapir.endpoint
import sttp.tapir.json.circe._
import sttp.tapir.path
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.statusCode
import sttp.tapir.stringBody

final class JsonSchemaCrudRoutes[F[_]: Async](repo: JsonSchemaRepositoryAlgebra[F]) extends Http4sDsl[F] {

  private val fetchRoute = JsonSchemaCrudRoutes.fetch.serverLogic[F](name =>
    repo.fetch(name).map {
      case Some(value) => value.document.asRight
      case None        => (StatusCode.NoContent, types.Response("", "", "", None)).asLeft
    }
  )

  private def uploadSchemaDocument(
      schemaId: SchemaId,
      jsonDocument: JsonDocument
  ): F[Either[(StatusCode, types.Response), (StatusCode, types.Response)]] =
    repo.insert(schemaId, jsonDocument).map {
      case Left(SchemaAlreadyExists(_)) =>
        (
          StatusCode.ImUsed,
          types.Response("uploadSchema", schemaId.value, "error", s"Schema already exists".some)
        ).asRight
      case Right(()) =>
        (StatusCode.Created, types.Response("uploadSchema", schemaId.value, "success", None)).asRight
    }

  private val insertRoute = JsonSchemaCrudRoutes.insert.serverLogic[F] { case (id, candidateJsonString) =>
    io.circe.parser.decode[JsonDocument](candidateJsonString) match {
      case Left(value) =>
        Async[F].delay(
          (StatusCode.BadRequest, types.Response("uploadSchema", id.value, "error", "Invalid Json".some)).asLeft
        )
      case Right(value) => uploadSchemaDocument(id, value)
    }
  }

  val routes: HttpRoutes[F] = Http4sServerInterpreter[F]()
    .toRoutes(
      List(
        fetchRoute,
        insertRoute
      )
    )
}

object JsonSchemaCrudRoutes {

  private val base = endpoint
    .in("schema")
    .in(path[SchemaId].description("SCHEMAID"))

  val fetch =
    base.get
      .description("Download a JSON Schema with unique `SCHEMAID`")
      .out(jsonBody[JsonDocument])
      .errorOut(statusCode.and(jsonBody[types.Response]))

  val insert =
    base.post
      .in(stringBody)
      .description("Upload a JSON Schema with unique `SCHEMAID`")
      .out(statusCode.and(jsonBody[types.Response]))
      .errorOut(statusCode.and(jsonBody[types.Response]))
}

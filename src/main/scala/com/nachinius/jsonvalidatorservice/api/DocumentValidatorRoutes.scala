package com.nachinius.jsonvalidatorservice.api

import cats.effect.Async
import cats.implicits.catsSyntaxEitherId
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toFunctorOps
import com.nachinius.jsonvalidatorservice.api.api._
import com.nachinius.jsonvalidatorservice.model.JsonDocument
import com.nachinius.jsonvalidatorservice.model.SchemaId
import com.nachinius.jsonvalidatorservice.model._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.codec.monix.newtype._
import sttp.tapir.endpoint
import sttp.tapir.json.circe._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.path
import sttp.tapir.server.http4s.Http4sServerInterpreter

final class DocumentValidatorRoutes[F[_]: Async](validator: DocumentValidatorAlgebra[F]) extends Http4sDsl[F] {

  val validateRoute = DocumentValidatorRoutes.base.serverLogic { case (id, document) =>
    for {
      schemaOrError <- validator.validate(id, document)
    } yield schemaOrError match {
      case Left(z: ErrorDuringValidation) =>
        types
          .Response(
            "validateDocument",
            id.value,
            "error",
            z.getMessage.some
          )
          .asRight[Unit]
      case Left(SchemaNotFound(_)) =>
        types
          .Response(
            "validateDocument",
            id.value,
            "error",
            "Schema doesn't exist".some
          )
          .asRight[Unit]
      case Right(_) =>
        types
          .Response(
            "validateDocument",
            id.value,
            "success",
            None
          )
          .asRight[Unit]
    }

  }

  val routes: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(validateRoute :: Nil)
}

object DocumentValidatorRoutes {

  val base = endpoint.post
    .in("validate")
    .in(path[SchemaId]("SchemaId"))
    .in(jsonBody[JsonDocument])
    .description("Validate a JSON document against the JSON Schema identified by `SCHEMAID`")
    .out(jsonBody[types.Response])
}

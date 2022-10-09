package com.nachinius.jsonvalidatorservice.implementations

import cats.Applicative
import com.nachinius.jsonvalidatorservice.model._
import cats.implicits.toFunctorOps
import cats._
import cats.implicits._

class DocumentValidatorService[F[_]: Applicative](
    repo: JsonSchemaRepositoryAlgebra[F],
    validator: ValidateJsonSchemaAlgebra
) extends DocumentValidatorAlgebra[F] {

  override def validate(schemaId: SchemaId, document: JsonDocument): F[Either[ValidatorError, Unit]] =
    for {
      maySchema <- repo.fetch(schemaId)
    } yield maySchema match {
      case Some(schema) => validator.validateDocument(schema, document)
      case None         => SchemaNotFound(schemaId.value).asLeft
    }
}

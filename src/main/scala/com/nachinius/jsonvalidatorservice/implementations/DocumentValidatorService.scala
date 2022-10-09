package com.nachinius.jsonvalidatorservice.implementations

import cats.Applicative
import com.nachinius.jsonvalidatorservice.model._
import cats.implicits.toFunctorOps

class DocumentValidatorService[F[_]: Applicative](
    repo: JsonSchemaRepositoryAlgebra[F],
    validator: ValidateJsonSchemaAlgebra
) extends DocumentValidatorAlgebra[F] {

  override def validate(schemaId: SchemaId, document: JsonDocument): F[Either[ValidatorError, Unit]] =
    repo
      .fetch(schemaId)
      .map(
        _.toRight(SchemaNotFound(schemaId.value))
          .flatMap(validator.validateDocument(_, document))
      )
}

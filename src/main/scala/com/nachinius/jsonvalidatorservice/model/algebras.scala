/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.model

trait ValidateJsonSchemaAlgebra {
  def validateDocument(schema: JsonSchema, document: JsonDocument): Either[ErrorDuringValidation, Unit]
}

trait DocumentValidatorAlgebra[F[_]] {
  def validate(schemaId: SchemaId, document: JsonDocument): F[Either[ValidatorError, Unit]]
}

trait JsonSchemaRepositoryAlgebra[F[_]] {
  def fetch(schemaId: SchemaId): F[Option[JsonSchema]]
  def insert(schemaId: SchemaId, document: JsonDocument): F[Either[SchemaExists, Unit]]
}

sealed abstract class ValidatorError(msg: String) extends Exception(msg)
case class ErrorDuringValidation(msg: String)     extends ValidatorError(msg)
case class SchemaNotFound(msg: String)            extends ValidatorError(s"Schema ${msg} not found")
case class SchemaExists(msg: String)              extends ValidatorError(s"Schema ${msg} already exists")

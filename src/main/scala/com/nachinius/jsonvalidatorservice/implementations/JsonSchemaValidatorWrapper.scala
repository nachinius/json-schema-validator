/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.implementations

import cats.implicits._
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.nachinius.jsonvalidatorservice.implementations.JsonSchemaValidatorWrapper.ProcessingException
import com.nachinius.jsonvalidatorservice.model._

import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Try

/** Wrapper for the java json schema validator library
  */
class JsonSchemaValidatorWrapper() extends ValidateJsonSchemaAlgebra {

  override def validateDocument(schema: JsonSchema, document: JsonDocument): Either[ErrorDuringValidation, Unit] =
    inner(schema, document).toEither.leftMap(ex => ErrorDuringValidation(ex.getMessage))

  def inner(schema: JsonSchema, document: JsonDocument): Try[Unit] =
    for {
      schemaJsonNode <- circeToJackson(schema.document.value)
      validator <- Try {
        JsonSchemaFactory.byDefault().getJsonSchema(schemaJsonNode)
      }
      jsonNode <- circeToJackson(document.value)
      response <- Try {
        validator.validate(jsonNode)
      }
    } yield
      if (response.isSuccess) ()
      else throw ProcessingException(response.iterator().asScala.map(_.getMessage).mkString(";"))

  def validateJsonSchema(document: JsonDocument): Either[ErrorDuringValidation, Unit] = {
    import com.github.fge.jsonschema.main.JsonSchemaFactory
    val validator = JsonSchemaFactory
      .byDefault()
      .getJsonSchema("resource:/draftv4/schema")
    val mayJsonNode = circeToJackson(document.value)
    mayJsonNode
      .flatMap(jsonNode => scala.util.Try(validator.validate(jsonNode)))
      .fold(
        ex => Left(ErrorDuringValidation(ex.getMessage)),
        pr =>
          if (pr.isSuccess) {
            Right(())
          } else {
            val msg = pr.iterator().asScala.map(_.getMessage).mkString(";")
            Left(ErrorDuringValidation(msg))
          }
      )
  }

  def circeToJackson(value: io.circe.Json): Try[JsonNode] = {
    val mapper = new ObjectMapper
    Try {
      mapper.readTree(value.noSpaces)
    }
  }
}

object JsonSchemaValidatorWrapper {

  import scala.util.control.NoStackTrace

  case class ProcessingException(msg: String) extends NoStackTrace
}

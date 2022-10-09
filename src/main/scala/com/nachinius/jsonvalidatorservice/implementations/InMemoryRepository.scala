/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.implementations

import cats._
import cats.data.State
import cats.effect._
import cats.implicits._
import com.nachinius.jsonvalidatorservice.implementations.InMemoryRepository.Storage
import com.nachinius.jsonvalidatorservice.model._

class InMemoryRepository[F[_]: Applicative](ref: Ref[F, Storage]) extends JsonSchemaRepositoryAlgebra[F] {
  override def fetch(schemaId: SchemaId): F[Option[JsonSchema]] =
    ref.get.map(_.get(schemaId).map(doc => JsonSchema(schemaId, doc)))

  override def insert(schemaId: SchemaId, document: JsonDocument): F[Either[RepositoryError, Unit]] =
    ref.modifyState {
      State.apply(s =>
        s.get(schemaId) match {
          case Some(_) => (s, Left(SchemaAlreadyExists(schemaId.value)))
          case None    => (s.updated(schemaId, document), Either.unit)
        }
      )
    }
}

object InMemoryRepository {
  type Storage = Map[SchemaId, JsonDocument]
  val empty: Storage = Map.empty[SchemaId, JsonDocument]

  import cats.effect.kernel.Ref.Make

  def make[F[_]: Make: Applicative]: F[JsonSchemaRepositoryAlgebra[F]] =
    for {
      ref <- Ref[F].of(empty)
    } yield new InMemoryRepository[F](ref)
}

package com.nachinius.jsonvalidatorservice.implementations

import cats.effect._
import cats.implicits._
import com.nachinius.jsonvalidatorservice.model.JsonDocument
import com.nachinius.jsonvalidatorservice.model.JsonSchema
import com.nachinius.jsonvalidatorservice.model.JsonSchemaRepositoryAlgebra
import com.nachinius.jsonvalidatorservice.model.SchemaAlreadyExists
import com.nachinius.jsonvalidatorservice.model.SchemaId
import com.nachinius.jsonvalidatorservice.model.UnknownError
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.util.transactor.Transactor
import cats._
import cats.data._
import cats.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.postgres.circe.json.implicits._
import cats._, cats.data._, cats.implicits._
import doobie._, doobie.implicits._
import io.circe._, io.circe.jawn._, io.circe.syntax._
import io.circe.Json
import com.nachinius.jsonvalidatorservice.model.RepositoryError

class PostgresRepository[F[_]: MonadCancelThrow](xa: Transactor[F]) extends JsonSchemaRepositoryAlgebra[F] {
  import PostgresRepository.Metas._
  override def fetch(schemaId: SchemaId): F[Option[JsonSchema]] =
    sql"SELECT id, doc FROM json_schema WHERE id = $schemaId".query[JsonSchema].option.transact(xa)

  override def insert(schemaId: SchemaId, document: JsonDocument): F[Either[RepositoryError, Unit]] = {
    (for {
      x <- sql"INSERT INTO json_schema (id, doc) values ($schemaId, $document)".update.run.attemptSqlState
      } yield x match {
        case Left(sqlstate.class23.UNIQUE_VIOLATION) => SchemaAlreadyExists(schemaId.value).asLeft[Unit]
        case Right(1) => import com.nachinius.jsonvalidatorservice.model.RepositoryError
          ().asRight[RepositoryError]
        case Left(ex) => UnknownError(s"SqlState=${ex.value}").asLeft[Unit]
        case Right(n) => UnknownError(s"Expected only one insert, found $n").asLeft[Unit]
      }).transact(xa)
  }

}

object PostgresRepository {
  type Storage = Map[SchemaId, JsonDocument]
  val empty: Storage = Map.empty[SchemaId, JsonDocument]


  def make[F[_]: Sync](xa: Transactor[F]): F[JsonSchemaRepositoryAlgebra[F]] =
    Sync[F].delay(new PostgresRepository[F](xa))

  object Metas {
    implicit val schemaIdMeta: Meta[SchemaId] = Meta[String].timap[SchemaId](
      (x: String) => SchemaId(x))(
      (schemaId: SchemaId) => schemaId.value
    )
    implicit val jsonDocumentReader: Read[JsonDocument] = Read[Json].map(JsonDocument(_))
    implicit val jsonDocumentPut: Put[JsonDocument] = Put[Json].contramap(_.value)
    implicit val jsonSchemaRead: Read[JsonSchema] = Read[(SchemaId,JsonDocument)].map {
      case (id, doc) => JsonSchema(id,doc)
    }
  }
}

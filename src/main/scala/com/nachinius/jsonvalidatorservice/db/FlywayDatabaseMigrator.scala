/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.db

import cats.effect.IO
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import com.nachinius.jsonvalidatorservice.types._

final class FlywayDatabaseMigrator {

  /** Apply pending migrations to the database.
    *
    * @param url
    *   A JDBC database connection url.
    * @param user
    *   The login name for the connection.
    * @param pass
    *   The password for the connection.
    * @return
    *   A migrate result object holding information about executed migrations and the schema. See the Java-Doc of Flyway
    *   for details.
    */
  def migrate(url: JdbcUrl, user: JdbcUsername, pass: JdbcPassword): IO[MigrateResult] =
    IO {
      val flyway: Flyway =
        Flyway.configure().dataSource(url.toString, user.toString, pass.toString).load()
      flyway.migrate()
    }

}

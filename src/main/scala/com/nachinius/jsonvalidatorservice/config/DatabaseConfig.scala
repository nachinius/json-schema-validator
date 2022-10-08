/*
 * Copyright (c) 2020 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.config

import com.nachinius.jsonvalidatorservice.types._
import pureconfig._

/**
  * The configuration for a database connection.
  *
  * @param driver The class name of the JDBC driver.
  * @param url    A JDBC URL.
  * @param user   The username for the connection.
  * @param pass   The password for the connection.
  */
final case class DatabaseConfig(
    driver: JdbcDriverName,
    url: JdbcUrl,
    user: JdbcUsername,
    pass: JdbcPassword
)

object DatabaseConfig {
  // The default configuration key to lookup the database configuration.
  final val CONFIG_KEY: ConfigKey = ConfigKey("database")

  given ConfigReader[JdbcDriverName] = ConfigReader.fromStringOpt(JdbcDriverName.from)
  given ConfigReader[JdbcPassword]   = ConfigReader.fromStringOpt(JdbcPassword.from)
  given ConfigReader[JdbcUrl]        = ConfigReader.fromStringOpt(JdbcUrl.from)
  given ConfigReader[JdbcUsername]   = ConfigReader.fromStringOpt(JdbcUsername.from)
  given ConfigReader[DatabaseConfig] = ConfigReader.forProduct4("driver", "url", "user", "pass")(DatabaseConfig.apply)

}

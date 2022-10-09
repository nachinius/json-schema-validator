/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice

import scala.util.matching.Regex

object types {

  type ConfigKey = String
  object ConfigKey {

    def apply(source: String): ConfigKey        = source
    def from(source: String): Option[ConfigKey] = Option(source).map(_.trim).filter(_.nonEmpty)

  }

  case class JdbcDriverName(value: String)
  object JdbcDriverName {
    val Format: Regex = "^\\w+\\.[\\w\\d\\.]+[\\w\\d]+$".r

    def apply(source: String): JdbcDriverName        = new JdbcDriverName(source)
    def from(source: String): Option[JdbcDriverName] = Option(source).filter(Format.matches).map(JdbcDriverName.apply)
  }

  case class JdbcPassword(value: String)
  object JdbcPassword {

    def apply(source: String): JdbcPassword        = new JdbcPassword(source)
    def from(source: String): Option[JdbcPassword] = Option(source).filter(_.nonEmpty).map(JdbcPassword.apply)

  }

  case class JdbcUrl(value: String)
  object JdbcUrl {
    import org.http4s.dsl.impl./
    val Format: Regex = "^jdbc:[a-zA-z0-9]+:.*".r

    def apply(source: String): JdbcUrl        = new JdbcUrl(source)
    def from(source: String): Option[JdbcUrl] = Option(source).filter(Format.matches).map(JdbcUrl.apply)
  }

  case class JdbcUsername(value: String)
  object JdbcUsername {
    def apply(source: String): JdbcUsername        = new JdbcUsername(source)
    def from(source: String): Option[JdbcUsername] = Option(source).filter(_.nonEmpty).map(JdbcUsername.apply)
  }
}

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

    /** Create an instance of ConfigKey from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a ConfigKey.
      * @return
      *   The appropriate instance of ConfigKey.
      */
    def apply(source: String): ConfigKey = source

    /** Try to create an instance of ConfigKey from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a ConfigKey.
      * @return
      *   An option to the successfully converted ConfigKey.
      */
    def from(source: String): Option[ConfigKey] = Option(source).map(_.trim).filter(_.nonEmpty)

  }

  case class JdbcDriverName(value: String)
  object JdbcDriverName {
    val Format: Regex = "^\\w+\\.[\\w\\d\\.]+[\\w\\d]+$".r

    /** Create an instance of JdbcDriverName from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a JdbcDriverName.
      * @return
      *   The appropriate instance of JdbcDriverName.
      */
    def apply(source: String): JdbcDriverName = new JdbcDriverName(source)

    /** Try to create an instance of JdbcDriverName from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a JdbcDriverName.
      * @return
      *   An option to the successfully converted JdbcDriverName.
      */
    def from(source: String): Option[JdbcDriverName] = Option(source).filter(Format.matches).map(JdbcDriverName.apply)
  }

  case class JdbcPassword(value: String)
  object JdbcPassword {

    /** Create an instance of JdbcPassword from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a JdbcPassword.
      * @return
      *   The appropriate instance of JdbcPassword.
      */
    def apply(source: String): JdbcPassword = new JdbcPassword(source)

    /** Try to create an instance of JdbcPassword from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a JdbcPassword.
      * @return
      *   An option to the successfully converted JdbcPassword.
      */
    def from(source: String): Option[JdbcPassword] = Option(source).filter(_.nonEmpty).map(JdbcPassword.apply)

  }

  case class JdbcUrl(value: String)
  object JdbcUrl {
    val Format: Regex = "^jdbc:[a-zA-z0-9]+:.*".r

    /** Create an instance of JdbcUrl from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a JdbcUrl.
      * @return
      *   The appropriate instance of JdbcUrl.
      */
    def apply(source: String): JdbcUrl = new JdbcUrl(source)

    /** Try to create an instance of JdbcUrl from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a JdbcUrl.
      * @return
      *   An option to the successfully converted JdbcUrl.
      */
    def from(source: String): Option[JdbcUrl] = Option(source).filter(Format.matches).map(JdbcUrl.apply)

  }

  case class JdbcUsername(value: String)
  object JdbcUsername {

    /** Create an instance of JdbcUsername from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a JdbcUsername.
      * @return
      *   The appropriate instance of JdbcUsername.
      */
    def apply(source: String): JdbcUsername = new JdbcUsername(source)

    /** Try to create an instance of JdbcUsername from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a JdbcUsername.
      * @return
      *   An option to the successfully converted JdbcUsername.
      */
    def from(source: String): Option[JdbcUsername] = Option(source).filter(_.nonEmpty).map(JdbcUsername.apply)
  }
}

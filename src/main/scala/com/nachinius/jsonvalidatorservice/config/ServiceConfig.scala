/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.config

import com.comcast.ip4s._
import com.nachinius.jsonvalidatorservice.types._
import pureconfig._

/** The service configuration.
  *
  * @param host
  *   The hostname the service will listen on.
  * @param port
  *   The port number the service will listen on.
  */
final case class ServiceConfig(host: Host, port: Port)

object ServiceConfig {
  // The default configuration key to lookup the service configuration.
  final val CONFIG_KEY: ConfigKey = ConfigKey("service")

  implicit val a: ConfigReader[Host]          = ConfigReader.fromStringOpt(Host.fromString)
  implicit val b: ConfigReader[Port]          = ConfigReader.fromStringOpt(Port.fromString)
  implicit val c: ConfigReader[ServiceConfig] = ConfigReader.forProduct2("host", "port")(ServiceConfig.apply)

}

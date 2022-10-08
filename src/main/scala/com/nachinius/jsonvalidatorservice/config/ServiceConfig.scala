/*
 * Copyright (c) 2020 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.config

import com.comcast.ip4s._
import pureconfig._
import com.nachinius.jsonvalidatorservice.types._

/**
  * The service configuration.
  *
  * @param host The hostname the service will listen on.
  * @param port The port number the service will listen on.
  */
final case class ServiceConfig(host: Host, port: Port)

object ServiceConfig {
  // The default configuration key to lookup the service configuration.
  final val CONFIG_KEY: ConfigKey = ConfigKey("service")

  given ConfigReader[Host]          = ConfigReader.fromStringOpt(Host.fromString)
  given ConfigReader[Port]          = ConfigReader.fromStringOpt(Port.fromString)
  given ConfigReader[ServiceConfig] = ConfigReader.forProduct2("host", "port")(ServiceConfig.apply)

}

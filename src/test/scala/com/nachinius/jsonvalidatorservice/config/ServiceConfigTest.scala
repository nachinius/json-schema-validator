/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.config

import com.typesafe.config.ConfigFactory
import munit._
import pureconfig._

class ServiceConfigTest extends FunSuite {

  test("ServiceConfig must load the default application.conf correctly") {
    val cfg = ConfigFactory.load(getClass().getClassLoader())
    ConfigSource.fromConfig(cfg).at(ServiceConfig.CONFIG_KEY.toString).load[ServiceConfig] match {
      case Left(e)  => fail(e.toList.mkString(", "))
      case Right(_) => assert(true)
    }
  }

}

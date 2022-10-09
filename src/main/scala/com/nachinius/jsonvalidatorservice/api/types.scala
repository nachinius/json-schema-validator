/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import sttp.tapir.Schema

object types {

  case class Response(actions: String, id: String, status: String, message: Option[String])

  object Response {

    import io.circe.generic.semiauto.deriveDecoder
    import io.circe.generic.semiauto.deriveEncoder

    implicit val decoder                               = deriveDecoder[Response]
    implicit val encoder                               = deriveEncoder[Response].mapJsonObject(_.filter(!_._2.isNull))
    implicit lazy val responseSchema: Schema[Response] = Schema.derived
  }

}

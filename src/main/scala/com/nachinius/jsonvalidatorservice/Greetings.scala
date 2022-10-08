/*
 * Copyright (c) 2020 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema
import com.nachinius.jsonvalidatorservice.types._

/**
  * A simple model for our hello world greetings.
  *
  * @param title    A generic title.
  * @param headings Some header which might be presented prominently to the user.
  * @param message  A message for the user.
  */
final case class Greetings(title: GreetingTitle, headings: GreetingHeader, message: GreetingMessage)

object Greetings {

  given Decoder[GreetingHeader] =
    Decoder.decodeString.emap(str => GreetingHeader.from(str).toRight("Invalid GreetingHeader!"))
  given Encoder[GreetingHeader] =
    Encoder.encodeString.contramap[GreetingHeader](_.toString)
  given Decoder[GreetingMessage] =
    Decoder.decodeString.emap(str => GreetingMessage.from(str).toRight("Invalid GreetingMessage!"))
  given Encoder[GreetingMessage] =
    Encoder.encodeString.contramap[GreetingMessage](_.toString)
  given Decoder[GreetingTitle] =
    Decoder.decodeString.emap(str => GreetingTitle.from(str).toRight("Invalid GreetingTitle!"))
  given Encoder[GreetingTitle] =
    Encoder.encodeString.contramap[GreetingTitle](_.toString)

  given Decoder[Greetings] = deriveDecoder[Greetings]
  given Encoder[Greetings] = deriveEncoder[Greetings]

  given Schema[Greetings] = Schema.derived[Greetings]

}

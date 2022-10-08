/*
 * Copyright (c) 2020 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import sttp.model._
import sttp.tapir._
import sttp.tapir.CodecFormat.TextPlain

object types {
  opaque type NameParameter = String
  object NameParameter {

    given Codec[String, NameParameter, TextPlain] =
      Codec.string.mapDecode(str =>
          NameParameter
            .from(str)
            .fold(
              sttp.tapir.DecodeResult.Error(str, new IllegalArgumentException("Invalid name parameter value!"))
            )(name => sttp.tapir.DecodeResult.Value(name))
          )(_.toString)

    /** Create an instance of NameParameter from the given String type.
      *
      * @param source
      *   An instance of type String which will be returned as a NameParameter.
      * @return
      *   The appropriate instance of NameParameter.
      */
    def apply(source: String): NameParameter = source

    /** Try to create an instance of NameParameter from the given String.
      *
      * @param source
      *   A String that should fulfil the requirements to be converted into a NameParameter.
      * @return
      *   An option to the successfully converted NameParameter.
      */
    def from(source: String): Option[NameParameter] = Option(source).filter(_.nonEmpty)
  }
}

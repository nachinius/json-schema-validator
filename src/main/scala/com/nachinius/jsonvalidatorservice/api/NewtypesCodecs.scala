/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Encoder
import io.circe.HCursor
import monix.newtypes.HasBuilder
import monix.newtypes.HasExtractor

trait NewtypesCodecs {

  // Inlined from https://github.com/monix/newtypes/blob/main/integration-circe/all/shared/src/main/scala/monix/newtypes/integrations/DerivedCirceCodec.scala
  // Cool usage of java SAM interface for the decoder :clap:
  implicit def newtypeCirceDecoder[New, Base](implicit
      builder: HasBuilder.Aux[New, Base],
      decoder: Decoder[Base]
  ): Decoder[New] = jsonDecode(_)

  protected def jsonDecode[T, S](
      c: HCursor
  )(implicit builder: HasBuilder.Aux[T, S], dec: Decoder[S]): Decoder.Result[T] =
    dec.apply(c).flatMap { value =>
      builder.build(value) match {
        case value @ Right(_) =>
          value.asInstanceOf[Either[DecodingFailure, T]]
        case Left(failure) =>
          val msg = failure.message.fold("")(m => s" — $m")
          Left(
            DecodingFailure(
              s"Invalid ${failure.typeInfo.typeLabel}$msg",
              c.history
            )
          )
      }
    }

  implicit def newtypeCirceEncoder[New, Base](implicit
      extractor: HasExtractor.Aux[New, Base],
      encoder: Encoder[Base]
  ): Encoder[New] = encoder.contramap(extractor.extract)
}

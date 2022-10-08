/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

package object api extends NewtypesCodecs {

  import io.circe.Codec
  import io.circe.generic.codec.DerivedAsObjectCodec
  import shapeless.Lazy

  type Codec[A] = io.circe.Codec[A]

  def deriveCodec[A](implicit codec: Lazy[DerivedAsObjectCodec[A]]): Codec.AsObject[A] = codec.value
}

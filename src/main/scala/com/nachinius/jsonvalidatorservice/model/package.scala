/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice

import monix.newtypes._
import io.circe.Json

package object model {

  type SchemaId = SchemaId.Type
  object SchemaId extends NewtypeWrapped[String]

  type JsonDocument = JsonDocument.Type
  object JsonDocument extends NewtypeWrapped[Json]

  case class JsonSchema(id: SchemaId, document: JsonDocument)
}

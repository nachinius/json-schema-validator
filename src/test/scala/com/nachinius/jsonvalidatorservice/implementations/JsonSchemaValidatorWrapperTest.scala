/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.implementations

import munit.FunSuite
import munit.FunSuite
import io.circe._
import com.nachinius.jsonvalidatorservice.model._

class JsonSchemaValidatorWrapperTest extends FunSuite {

  val service: JsonSchemaValidatorWrapper = new JsonSchemaValidatorWrapper()

  val document = JsonDocument("""
    {
      "$schema": "http://json-schema.org/draft-04/schema#",
      "title": "/etc/fstab",
      "description": "JSON representation of /etc/fstab",
      "type": "object",
      "properties": {
          "swap": {
              "$ref": "#/definitions/mntent"
          }
      },
      "patternProperties": {
          "^/([^/]+(/[^/]+)*)?$": {
              "$ref": "#/definitions/mntent"
          }
      },
      "required": [ "/", "swap" ],
      "additionalProperties": false,
      "definitions": {
          "mntent": {
              "title": "mntent",
              "description": "An fstab entry",
              "type": "object",
              "properties": {
                  "device": {
                      "type": "string"
                  },
                  "fstype": {
                      "type": "string"
                  },
                  "options": {
                      "type": "array",
                      "minItems": 1,
                      "items": { "type": "string" }
                  },
                  "dump": {
                      "type": "integer",
                      "minimum": 0
                  },
                  "fsck": {
                      "type": "integer",
                      "minimum": 0
                  }
              },
              "required": [ "device", "fstype" ],
              "additionalItems": false
          }
      }
  }
    """.asJson)

  test("validate a valid json v4 schema with document") {
    val doc =
      JsonDocument("""
        {
            "/": {
                "device": "/dev/sda1",
                "fstype": "btrfs",
                "options": [ "ssd" ]
            },
            "swap": {
                "device": "/dev/sda2",
                "fstype": "swap"
            },
            "/tmp": {
                "device": "tmpfs",
                "fstype": "tmpfs",
                "options": [ "size=64M" ]
            },
            "/var/lib/mysql": {
                "device": "/dev/data/mysql",
                "fstype": "btrfs"
            }
        }
        """.asJson)
    val triedString = service.inner(JsonSchema(SchemaId(""), doc), doc)
    assert(triedString.isSuccess)
  }

  test("validate a document against v4") {
    val validationOrUnit = service.validateJsonSchema(document)
    assert(validationOrUnit.isRight)
  }

  private implicit class toJson(str: String) {
    def asJson: Json =
      io.circe.parser.parse(str) match {
        case Left(value)  => throw new IllegalArgumentException(s"invalid json ${str} because ${value}")
        case Right(value) => value
      }
  }
}

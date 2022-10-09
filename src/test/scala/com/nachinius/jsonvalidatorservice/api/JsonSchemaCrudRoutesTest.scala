package com.nachinius.jsonvalidatorservice.api

import cats.effect._
import munit._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router
class JsonSchemaCrudRoutesTest extends CatsEffectSuite {

  test("Valid JSON Schema Upload".ignore) {}
  test("Invalid JSON Schema Upload".ignore) {}
  test("Fetch JSON Schema".ignore) {}
  test("JSON Schema already exist".ignore) {}
}

class DocumentValidatorRoutesTest extends CatsEffectSuite {
  test("Valid document".ignore) {}
  test("Invalid document".ignore) {}
  test("JSON Schema doesn't exist") {}
}

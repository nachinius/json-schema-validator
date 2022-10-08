/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import cats.effect._
import com.nachinius.jsonvalidatorservice.Greetings
import com.nachinius.jsonvalidatorservice.types._
import munit._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

class HelloWorldTest extends CatsEffectSuite {

  implicit def decodeGreetings: EntityDecoder[IO, Greetings] = jsonOf

  test("when parameter 'name' is missing") {
    val expectedStatusCode = Status.BadRequest

    Uri.fromString("/hello") match {
      case Left(_) =>
        fail("Could not generate valid URI!")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new HelloWorld[IO].routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, "Invalid value for: query parameter name (missing)"))
    }
  }

  test("when parameter 'name' is invalid") {
    val expectedStatusCode = Status.BadRequest

    Uri.fromString("/hello?name=") match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new HelloWorld[IO].routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[String]
        } yield (result.status, body)
        test.assertEquals(
          (
            expectedStatusCode,
            "Invalid value for: query parameter name"
          )
        )
    }
  }

  test("when parameter 'name' is valid") {
    val expectedStatusCode = Status.Ok

    val name = "Captain Kirk"
    val expectedGreetings = Greetings(
      title = "Hello Captain Kirk!",
      headings = "Hello Captain Kirk, live long and prosper!",
      message = "This is a fancy message directly from http4s! :-)"
    )
    Uri.fromString(Uri.encode(s"/hello?name=$name")) match {
      case Left(e) =>
        fail(s"Could not generate valid URI: $e")
      case Right(u) =>
        def service: HttpRoutes[IO] = Router("/" -> new HelloWorld[IO].routes)
        val request = Request[IO](
          method = Method.GET,
          uri = u
        )
        val response = service.orNotFound.run(request)
        val test = for {
          result <- response
          body   <- result.as[Greetings]
        } yield (result.status, body)
        test.assertEquals((expectedStatusCode, expectedGreetings))
    }
  }
}

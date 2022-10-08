/*
 * Copyright (c) 2022 nachinius
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nachinius.jsonvalidatorservice.api

import cats.effect._
import cats.syntax.all._
import com.nachinius.jsonvalidatorservice.Greetings
import com.nachinius.jsonvalidatorservice.api.types._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import sttp.model._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

final class HelloWorld[F[_]: Async] extends Http4sDsl[F] {
//  final val message: GreetingMessage = GreetingMessage("This is a fancy message directly from http4s! :-)")

  implicit def decodeGreetings: EntityDecoder[F, Greetings] = jsonOf
  implicit def encodeGreetings: EntityEncoder[F, Greetings] = jsonEncoderOf

  private val sayHello: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(HelloWorld.greetings.serverLogic { name =>
//      val greetings = (
//        GreetingTitle.from(s"Hello $name!"),
//        GreetingHeader.from(s"Hello $name, live long and prosper!")
//      ).mapN { case (title, headings) =>
//        Greetings(
//          title = title,
//          headings = headings,
//          message = message
//        )
//      }
      Sync[F].delay(Option(HelloWorld.example).fold(StatusCode.BadRequest.asLeft[Greetings])(_.asRight[StatusCode]))
    })

  val routes: HttpRoutes[F] = sayHello

}

object HelloWorld {
  val example = Greetings("a", "b", "c")

  val greetings: Endpoint[Unit, NameParameter, StatusCode, Greetings, Any] =
    endpoint.get
      .in("hello")
      .in(query[NameParameter]("name"))
      .errorOut(statusCode)
      .out(jsonBody[Greetings].description("A JSON object demo").example(example))
      .description(
        "Returns a simple JSON object using the provided query parameter 'name' which must not be empty."
      )
}

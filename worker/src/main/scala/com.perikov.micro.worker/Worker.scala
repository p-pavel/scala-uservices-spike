package com.perikov.micro
package worker

import cats.effect.{IOApp, IO}
import com.perikov.micro.utils.grpc
import com.comcast.ip4s.port

import scribe.cats.*

object Worker extends IOApp.Simple:

  def run =
    echoImpl[IO]
    .flatMap(grpc.server(port"18080", _)) //TODO: port from config
    .useForever

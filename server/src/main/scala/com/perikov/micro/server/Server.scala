package com.perikov.micro
package server

import scala.concurrent.duration.FiniteDuration

import cats.effect.{Async, Resource, IOApp, IO, Clock}
import cats.{Monad, Functor}
import cats.syntax.flatMap.*

import com.comcast.ip4s.{host,ipv4, port}

import scribe.*
import scribe.cats.*

import com.perikov.micro.utils.{http, grpc}

def servers[F[_]: Async]: Resource[F, (http.Server, EchoClient[F])] =
  for
    http <- http.server[F](port"8080", simpleHttpApp) //TODO: from config
    echo <- echoClient("worker", port"18080") //TODO: from config
  yield (http, echo)

def callRepeated[F[_]: Monad](n: Long, client: EchoClient[F]): F[Unit] =
  if (n == 0) Monad[F].unit
  else client.echo("Hello", n) >> callRepeated(n - 1, client)

object Server extends IOApp.Simple:

  extension [A](act: IO[A])
    def measureTime: IO[FiniteDuration] = Clock[IO].timed(act).map(_._1)
  def run: IO[Unit]                     =
    servers[IO] use { (_, echo) =>
      val logger = Scribe[IO]
      val numCalls = 100_000
      for
        _        <- logger.info(s"Got resources. Start issuing $numCalls calls.")
        duration <- callRepeated(numCalls, echo).measureTime
        _        <- logger.info(s"Done in $duration")
        _        <- logger.info(s"Call duration: ${duration / numCalls}. Press enter to exit"
                    )
      yield ()
    }

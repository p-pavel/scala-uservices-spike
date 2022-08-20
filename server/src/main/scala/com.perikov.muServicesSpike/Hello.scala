package ru.perikov.muServicesSpike

import scala.concurrent.duration.FiniteDuration

import cats.effect.{Async, Resource, IOApp, IO, Clock}
import cats.{Monad, Functor}
import cats.syntax.flatMap.*

import org.typelevel.log4cats
import log4cats.Logger
import log4cats.syntax.*
import log4cats.slf4j.*

import utils.{http, grpc}

def servers[F[_]: Async]
    : Resource[F, (http.Server, grpc.Server, EchoClient[F])] =
  import com.comcast.ip4s.{ipv4, port}
  for
    http <- http.server[F](port"8080", simpleHttpApp)
    echo <- echoImpl[F]
    grpc <- grpc.server[F](port"18080", echo)
    echo <- echoClient(ipv4"127.0.0.1", port"18080")
  yield (http, grpc, echo)

def callRepeated[F[_]: Monad](n: Long, client: EchoClient[F]): F[Unit] =
  if (n == 0) Monad[F].unit
  else client.echo("Hello", n) >> callRepeated(n - 1, client)



object Hello extends IOApp.Simple:

  extension [A](act: IO[A]) def measureTime: IO[FiniteDuration] = Clock[IO].timed(act).map(_._1)
  def run: IO[Unit] =
    servers[IO] use { (_, _, echo) =>
      val numCalls = 100_000
      for
        logger          <- log4cats.slf4j.Slf4jLogger.create[IO]
        given Logger[IO] = logger
        _               <- info"Got resources. Start issuing $numCalls calls."
        duration        <- callRepeated(numCalls, echo).measureTime
        _               <- info"Done in $duration"
        _               <- info"Call duration: ${duration / numCalls}. Press enter to exit"
        _               <- IO.readLine
      yield ()
    }

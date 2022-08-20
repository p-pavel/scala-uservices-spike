import cats.effect.{Async, Resource, IOApp, IO}
import cats.Monad
import cats.syntax.flatMap.*
import scribe.{Logger, Level}

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

def callTimes[F[_]: Monad](n: Int, client: EchoClient[F]): F[Unit] =
  if (n == 0) Monad[F].unit
  else client.echo("Hello") >> callTimes(n - 1, client)

object Hello extends IOApp.Simple:
  scribe.Logger.root.withMinimumLevel(Level.Info).replace()

  def run: IO[Unit] =
    import concurrent.duration.*
    import fs2.*
    servers[IO] use { (_, _, echo) =>
      for
        _     <- IO.println("Got resources. Start calling.")
        start <- IO.realTime
        numCalls = 100000
        _     <- callTimes(numCalls, echo)
        stop  <- IO.realTime
        duration = stop - start
        _     <- IO.println(s"Done in $duration")
        _ <- IO.println(s"Call duration: ${duration / numCalls}")
      yield ()
    }

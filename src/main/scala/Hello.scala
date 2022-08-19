import cats.effect.*
import cats.effect.implicits.*

import scribe.*
import scribe.format.*

import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.dsl.*
import org.http4s.server.Router

import com.comcast.ip4s.*

def app[F[_]: Async] =
  val dsl = Http4sDsl[F]
  import dsl.*
  HttpRoutes
    .of[F] { 
      case GET -> Root  => Ok("Hello!")
    }
    .orNotFound

def blazeServer[F[_]: Async] =
  EmberServerBuilder.default
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(app[F])
    .build

object Hello extends IOApp.Simple:
  scribe.Logger.root.withMinimumLevel(Level.Debug).replace()

  def run: IO[Unit] =
    blazeServer[IO].use { s =>
      IO.never
    }

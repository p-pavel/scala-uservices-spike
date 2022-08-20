import cats.effect.Async
import org.http4s.dsl.*
import org.http4s.{HttpRoutes, HttpApp}
import org.http4s.Response
import org.http4s.Request

def simpleHttpApp[F[_]: Async]: HttpApp[F] =
  val dsl = Http4sDsl[F]
  import dsl.*
  HttpRoutes
    .of[F] { case req @ GET -> Root =>
      Ok("Hello!")
    }
    .orNotFound

package com.perikov.micro.utils

object http:
  import com.comcast.ip4s.Literals.ipv4
  import cats.effect.{Resource, Async}
  import com.comcast.ip4s.{Port, ipv4}
  import org.http4s.HttpApp
  import org.http4s.server.Server
  import org.http4s.ember.server.EmberServerBuilder

  export org.http4s.HttpApp
  export org.http4s.server.Server
  export cats.effect.{Resource, Async}
  export com.comcast.ip4s.Port

  def server[F[_]: Async](port: Port, app: HttpApp[F]): Resource[F, Server] =
    EmberServerBuilder.default
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withHttpApp(app)
      .build

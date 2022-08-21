package com.perikov.micro.server

import cats.*
import cats.syntax.all.*
import cats.effect.*
import org.http4s.dsl.*
import org.http4s.{HttpRoutes, HttpApp}

def simpleHttpApp[F[_]: Async]: HttpApp[F] =
  val dsl = Http4sDsl[F]
  import dsl.*
  HttpRoutes
    .of[F] {
     case req @ GET -> Root =>
      Ok("Hello!")
    case GET -> Root / "metrics" => 
      for 
        s <- Metrics[F].scrape 
        res <- Ok(s)
      yield res
    }
    .orNotFound

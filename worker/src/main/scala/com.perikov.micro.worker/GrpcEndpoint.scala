package com.perikov.micro.worker

import cats.Monad
import cats.syntax.all.*
import cats.effect.{Async, Resource, Ref}
import io.grpc.{ServerServiceDefinition, Metadata}
import scribe.Scribe
import scribe.cats.*

import Test.{TestServiceFs2Grpc, MessageName}

private def mkEchoImpl[F[_]: Scribe: Monad](pingCount: Ref[F, Long]) =
  new TestServiceFs2Grpc[F, Metadata]:
    override def echo(request: MessageName, ctx: Metadata): F[MessageName] =
      for
        cnt <- pingCount.updateAndGet(_ + 1)
        _   <- Scribe[F].info(s"Echo request count = %cnt").whenA(cnt % 1000 == 0)
      yield MessageName(request.msg.reverse, cnt)

def echoImpl[F[_]: Async: Scribe]: Resource[F, ServerServiceDefinition] =
  Resource
    .eval(Ref.of[F, Long](0))
    .map(mkEchoImpl)
    .flatMap(TestServiceFs2Grpc.bindServiceResource)

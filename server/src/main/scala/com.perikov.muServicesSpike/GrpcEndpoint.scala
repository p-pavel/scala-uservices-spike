package ru.perikov.muServicesSpike

import cats.syntax.functor.*
import cats.effect.{Async, Resource}

import fs2.grpc.syntax.managedChannelBuilder.*

import Test.*

import io.grpc.{Metadata, ServerServiceDefinition, ManagedChannelBuilder}

trait EchoClient[F[_]]:
    def echo(s: String, num: Long): F[String]

def echoImpl[F[_]: Async]: Resource[F, ServerServiceDefinition] =
  TestServiceFs2Grpc.bindServiceResource(
    new TestServiceFs2Grpc[F, Metadata]:
      override def echo(request: MessageName, ctx: Metadata): F[MessageName] =
        Async[F].pure(MessageName(request.msg.reverse, request.messageNumber))
  )

import com.comcast.ip4s.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.ManagedChannelBuilder



def echoClient[F[_]:Async](host: Ipv4Address, port: Port): Resource[F, EchoClient[F]] = 
   ManagedChannelBuilder
    .forAddress(host.toString, port.value)
    .usePlaintext()
    .resource
    .flatMap( TestServiceFs2Grpc.stubResource)
    .map { svc => 
        new EchoClient[F]:
            def echo(s: String, num: Long) = svc.echo(MessageName(s, num), Metadata()).map(_.msg)
        }

package com.perikov.micro.server
import com.perikov.micro.utils.grpc.{channelResource}

import cats.syntax.functor.*
import cats.effect.{Async, Resource}

import fs2.grpc.syntax.managedChannelBuilder.*
import io.grpc.{Metadata, ServerServiceDefinition, ManagedChannelBuilder, ManagedChannel}
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder

import com.comcast.ip4s.{Ipv4Address, Port}

import Test.{TestServiceFs2Grpc,MessageName}


trait EchoClient[F[_]]:
    def echo(s: String, num: Long): F[String]

def echoClient[F[_]:Async](host: String, port: Port): Resource[F, EchoClient[F]] = 
    channelResource(host, port)
    .flatMap( TestServiceFs2Grpc.stubResource)
    .map { svc => 
        new EchoClient[F]:
            def echo(s: String, num: Long) = svc.echo(MessageName(s, num), Metadata()).map(_.msg)
        }

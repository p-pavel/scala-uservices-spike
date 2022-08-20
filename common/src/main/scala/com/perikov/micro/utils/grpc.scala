package com.perikov.micro.utils

object grpc:
  import com.comcast.ip4s.{Port, Ipv4Address}
  import fs2.grpc.syntax.all.*
  import io.grpc.{ServerServiceDefinition, Server, ManagedChannel, ManagedChannelBuilder}
  import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
  import io.grpc.protobuf.services.ProtoReflectionService
  import cats.effect.{Async, Sync, Resource}
  import scribe.Scribe
  import scribe.cats.{io => _, *}

  export io.grpc.Server

  def server[F[_]: Scribe: Sync](
      port: Port,
      services: ServerServiceDefinition*
  ): Resource[F, Server] =
    import scala.jdk.CollectionConverters.*
    NettyServerBuilder
      .forPort(port.value)
      .addService(ProtoReflectionService.newInstance())
      .addServices(services.asJava)
      .resource[F]
      .evalMap(server => Sync[F].delay(server.start))
      .evalTap(_ => Scribe[F].info(s"GRPC server started on port $port"))

  def channelResource[F[_]: Async](
      host: String,
      port: Port
  ): Resource[F, ManagedChannel] =
    ManagedChannelBuilder
      .forAddress(host, port.value)
      .usePlaintext()
      .resource

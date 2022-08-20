package ru.perikov.muServicesSpike.utils

object grpc:
    import com.comcast.ip4s.{Port}
    import io.grpc.{ServerServiceDefinition,Server}
    import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
    import cats.effect.{Sync,Resource}
    import fs2.grpc.syntax.serverBuilder.*
    export io.grpc.Server
    import scribe.Scribe
    import scribe.format.*

    def server[F[_]: Sync](port: Port, services: ServerServiceDefinition*): Resource[F, Server] = 
        import scala.jdk.CollectionConverters.*
          NettyServerBuilder.forPort(port.value)
            .addService(io.grpc.protobuf.services.ProtoReflectionService.newInstance())
            .addServices(services.asJava)
            .resource[F]
            .evalMap(server=> Sync[F].delay(server.start))
            .evalTap(_ => Sync[F].delay(scribe.info(s"GRPC server started on port $port")))
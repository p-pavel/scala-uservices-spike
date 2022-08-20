import cats.effect.{Async, Resource}
import fs2.grpc.syntax.managedChannelBuilder.*
import cats.syntax.functor.*
import Test.*

import io.grpc.{Metadata, ServerServiceDefinition, ManagedChannelBuilder}

trait EchoClient[F[_]]:
    def echo(s: String): F[String]

def echoImpl[F[_]: Async]: Resource[F, ServerServiceDefinition] =
  TestServiceFs2Grpc.bindServiceResource(
    new TestServiceFs2Grpc[F, Metadata]:
      override def echo(request: MessageName, ctx: Metadata): F[MessageName] =
        Async[F].pure(MessageName(request.msg.reverse))
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
            def echo(s: String) = svc.echo(MessageName(s), Metadata()).map(_.msg)
        }

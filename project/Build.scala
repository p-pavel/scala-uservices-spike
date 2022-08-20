import sbt.*

object Versions {
  val circeVersion  = "0.14.1"
  val http4sVersion = "1.0.0-M35"
  val fs2Version    = "3.2.12"
  val scribeVersion = "3.10.2"
}

object Components {
  import Versions.*
  val scribe =
    Seq("scribe-cats", "scribe-slf4j").map("com.outr" %% _ % scribeVersion)

  val grpc =
    Seq("grpc-netty-shaded", "grpc-services")
      .map("io.grpc" % _ % scalapb.compiler.Version.grpcJavaVersion)

  val http4s =
    Seq("http4s-ember-server", "http4s-circe", "http4s-dsl")
      .map("org.http4s" %% _ % http4sVersion)

  val circe = Seq("circe-core", "circe-generic", "circe-parser")
    .map("io.circe" %% _ % circeVersion)

  val fs2 = Seq("fs2-core", "fs2-io").map("co.fs2" %% _ % fs2Version)
}

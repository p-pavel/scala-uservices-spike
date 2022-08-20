import sbt.*
import sbt.librarymanagement.DependencyBuilders

object Docker {
  val baseImage = "eclipse-temurin:11-jre"
}

object Versions {
  val scala    = "3.1.3"
  val circe    = "0.14.1"
  val http4s   = "1.0.0-M35"
  val fs2      = "3.2.12"
  val scribe   = "3.10.2"
}

object Components {
  private def artifacts(prefix: String, version: String)(s: String*) =
    s.map(prefix %% _ % version)

  private def jArtifacts(prefix: String, version: String)(s: String*) =
    s.map(prefix % _ % version)

  val scribe =
    artifacts("com.outr", Versions.scribe)("scribe-cats", "scribe-slf4j")

  val grpc   = jArtifacts("io.grpc", scalapb.compiler.Version.grpcJavaVersion)(
    "grpc-netty-shaded",
    "grpc-services"
  )
  val http4s = artifacts("org.http4s", Versions.http4s)(
    "http4s-ember-server",
    "http4s-circe",
    "http4s-dsl"
  )

  val circe = artifacts("io.circe", Versions.circe)(
    "circe-core",
    "circe-generic",
    "circe-parser"
  )
  val fs2   = artifacts("co.fs2", Versions.fs2)("fs2-core", "fs2-io")
}

import sbt.*

object Versions {
  val circeVersion = "0.14.1"
  val http4sVersion = "1.0.0-M35"
  val fs2Version = "3.2.12"
  val scribeVersion = "3.10.2"
}

object Components {
  import Versions.*
  val scribe = Seq(
    "com.outr" %% "scribe-cats",
    "com.outr" %% "scribe-slf4j"
  ).map(_ % scribeVersion)

  val http4s =
    Seq(
      "org.http4s" %% "http4s-ember-server",
      "org.http4s" %% "http4s-circe",
      "org.http4s" %% "http4s-dsl"
    ).map(_ % http4sVersion)

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val fs2 = Seq(
    "co.fs2" %% "fs2-core",
    "co.fs2" %% "fs2-io"
  ).map(_ % fs2Version)
}

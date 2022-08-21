import Components.*

lazy val `grpc-protocols` =
  project
    .in(file("grpc"))
    .enablePlugins(Fs2Grpc)
    .settings(
      scalaVersion := Versions.scala,
      name         := "grpc-api",
      version      := "0.0.1"
    )

lazy val common =
  project
    .in(file("common"))
    .dependsOn(`grpc-protocols`)//TODO: strange dependency needed to have fs2.grpc.
    .settings(
      scalaVersion := Versions.scala,
      name         := "common",
      version      := "0.0.1",
      libraryDependencies ++= scribe ++ grpc ++  http4s
    )

lazy val worker =
  project
    .in(file("worker"))
    .enablePlugins(JavaAppPackaging, DockerPlugin)
    .dependsOn(`grpc-protocols`, common)
    .settings(
      scalaVersion       := "3.1.3",
      name               := "spike-worker",
      version            := "0.0.1",
      libraryDependencies ++= scribe ++ grpc,
      dockerBaseImage    := Docker.baseImage,
      dockerExposedPorts := Seq(18080)
    )

lazy val server =
  project
    .in(file("server"))
    .enablePlugins(JavaAppPackaging, DockerPlugin)
    .dependsOn(`grpc-protocols`, common)
    .settings(
      scalaVersion       := "3.1.3",
      name               := "spike-server",
      version            := "0.0.1",
      dockerBaseImage    := Docker.baseImage,
      dockerExposedPorts := Seq(8080),
      libraryDependencies ++= http4s ++ scribe ++ grpc ++ prometheus
    )

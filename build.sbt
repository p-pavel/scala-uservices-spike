import Components.*


lazy val `grpc-protocols` = 
  project
  .in(file("grpc"))
  .enablePlugins( Fs2Grpc)
  .settings(
    scalaVersion := "3.1.3",
    name := "grpc-api",
    version := "0.0.1"
  )

lazy val client = 
  project
  .in(file("client")) 
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .dependsOn(`grpc-protocols`)
  .settings(
    scalaVersion := "3.1.3",
    name := "spike-client",
    version := "0.0.1",

    libraryDependencies ++=  scribe ++ grpc,

    dockerBaseImage := Docker.baseImage,
    dockerExposedPorts := Seq(18080)
  )

lazy val server =
  project
    .in(file("server"))
    .enablePlugins(JavaAppPackaging, DockerPlugin)
    .dependsOn(`grpc-protocols`)
    .settings(
      scalaVersion := "3.1.3",
      name := "spike-server",
      version := "0.0.1",

      dockerBaseImage := Docker.baseImage,
      dockerExposedPorts := Seq(8080),

      libraryDependencies ++= circe ++ http4s ++ fs2 ++ scribe ++ grpc
    )

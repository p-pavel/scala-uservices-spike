import Components.*


lazy val grpc = 
  project
  .in(file("grpc"))
  .enablePlugins( Fs2Grpc)
  .settings(
    scalaVersion := "3.1.3",
    name := "grpc-api",
    version := "0.0.0.1"
  )

lazy val root =
  project
    .in(file("."))
    .enablePlugins(JavaAppPackaging, DockerPlugin)
    .dependsOn(grpc)
    .settings(
      scalaVersion := "3.1.3",
      name := "spike",
      version := "0.0.1",

      dockerExposedPorts := Seq(8080, 18080),
      dockerBaseImage := "eclipse-temurin:11",

      libraryDependencies ++= circe ++ http4s ++ fs2 ++ scribe ++ Components.grpc,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test",
    )

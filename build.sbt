import Components.*

enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "spike",
      version := "0.0.1",
      dockerExposedPorts := Seq(8080),
      dockerBaseImage := "eclipse-temurin:11",
      scalaVersion := "3.1.3",
      libraryDependencies ++= circe ++ http4s ++ fs2 ++ scribe,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"
    )

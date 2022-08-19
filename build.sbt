import Components.*

lazy val root =
  project
    .in(file("."))
    .settings(
      scalaVersion := "3.1.3",
      libraryDependencies ++= circe ++ http4s ++ fs2 ++ scribe,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test"
    )

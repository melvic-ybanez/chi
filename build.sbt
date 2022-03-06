ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "chi",
    libraryDependencies ++= Seq(
        "org.scala-lang.modules" % "scala-parser-combinators_2.13" % "2.1.1"
    )
  )

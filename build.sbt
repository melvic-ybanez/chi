ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "chi",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % "2.2.2" // SBT
    )
  )

import Dependencies.dependencies

ThisBuild / version := "0.1.0-RC2"

ThisBuild / scalaVersion := "2.13.8"

assemblyJarName in assembly := "chi-0.1.0-rc2.jar"


lazy val root = (project in file("."))
  .settings(
    name := "chi",
    libraryDependencies ++= dependencies
  )

import Dependencies.dependencies

ThisBuild / version := "0.5.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

assemblyJarName in assembly := "chi-0.5.0-snapshot.jar"

lazy val root = (project in file("."))
  .settings(
    name := "chi",
    libraryDependencies ++= dependencies
  )

// Fix deduplication error during merge (see https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

import Dependencies.dependencies

ThisBuild / version := "0.6.1"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    assembly / assemblyJarName := "chi-0.6.1.jar",
    name := "chi",
    libraryDependencies ++= dependencies
  )

// Fix deduplication error during merge (see https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

import Dependencies.dependencies

ThisBuild / version := "0.5.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

<<<<<<< HEAD
=======
assemblyJarName in assembly := "chi-0.5.0-snapshot.jar"

>>>>>>> 7edb07a443d01a30766099e3b70f02bbf588913a
lazy val root = (project in file("."))
  .settings(
    assembly / assemblyJarName := "chi-0.4.0-snapshot.jar",
    name := "chi",
    libraryDependencies ++= dependencies
  )

// Fix deduplication error during merge (see https://stackoverflow.com/questions/25144484/sbt-assembly-deduplication-found-error)
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

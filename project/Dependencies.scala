import sbt._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.scalactic" %% "scalactic" % "3.2.11",
    "org.scalatest" %% "scalatest" % "3.2.11" % "test",
  )

  lazy val dependencies = Seq(
    "org.scala-lang.modules" % "scala-parser-combinators_2.13" % "2.1.1",
    "com.fifesoft" % "rsyntaxtextarea" % "3.2.0"
  ) ++ testDependencies
}

import sbt._

object Dependencies {
  lazy val testDependencies = Seq(
    "org.scalactic" %% "scalactic" % "3.2.11",
    "org.scalatest" %% "scalatest" % "3.2.11" % "test",
  )

  lazy val uiDependencies = Seq(
    "com.github.weisj" % "darklaf-core" % "2.7.3",
    "com.fifesoft" % "rsyntaxtextarea" % "3.2.0"
  )

  lazy val jsonDependencies = Seq(
    "com.lihaoyi" %% "upickle" % "1.5.0"
  )

  lazy val dependencies = Seq(
    "org.scala-lang.modules" % "scala-parser-combinators_2.13" % "2.1.1",
    "com.lihaoyi" %% "os-lib" % "0.8.1",
    "com.miglayout" % "miglayout-swing" % "11.0"
  ) ++ testDependencies ++ uiDependencies ++ jsonDependencies
}

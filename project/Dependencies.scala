import sbt._

object Dependencies {
  lazy val javaFxDeps = {
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux") => "linux"
      case n if n.startsWith("Mac") => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
      .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
  }

  lazy val fxDeps = Seq(
    "org.scalafx" %% "scalafx" % "16.0.0-R24",
    "org.fxmisc.richtext" % "richtextfx" % "0.10.9"
  ) ++ javaFxDeps
}

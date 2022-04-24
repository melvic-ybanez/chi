package com.melvic.chi

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.DarculaTheme
import com.melvic.chi.config.Preferences
import com.melvic.chi.views.MainWindow

import javax.swing.SwingUtilities
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Main {
  implicit val preferences: Preferences = Preferences.load

  def main(args: Array[String]): Unit =
    args match {
      case Array("repl", _ @_*) => Repl.start(println, readLine(_))
      case _                    => runUI()
    }

  def runUI(): Unit =
    SwingUtilities.invokeLater(new Runnable() {
      LafManager.install(new DarculaTheme)

      override def run(): Unit = {
        new MainWindow().setVisible(true)
      }
    })
}

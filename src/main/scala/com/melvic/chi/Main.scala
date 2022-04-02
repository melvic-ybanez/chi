package com.melvic.chi

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.DarculaTheme
import com.melvic.chi.views.menus.MainView

import javax.swing.SwingUtilities

//noinspection SpellCheckingInspection
object Main {
  def main(args: Array[String]): Unit =
    args match {
      case Array("repl", _ @_*) => Repl()
      case _                    => runUI()
    }

  def runUI(): Unit =
    SwingUtilities.invokeLater(new Runnable() {
      LafManager.install(new DarculaTheme)

      override def run(): Unit = {
        new MainView(generateAndShow).setVisible(true)
      }
    })
}

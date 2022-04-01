package com.melvic.chi

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.DarculaTheme
import com.melvic.chi.views.MainView

import javax.swing.SwingUtilities
import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Main {
  def main(args: Array[String]): Unit = {
    //Repl()
    SwingUtilities.invokeLater(new Runnable() {
      LafManager.install(new DarculaTheme)

      override def run(): Unit = {
        new MainView().setVisible(true)
      }
    })
  }
}

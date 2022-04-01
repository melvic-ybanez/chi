package com.melvic.chi

import com.melvic.chi.views.EditorView

import javax.swing.SwingUtilities
import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Main {
  def main(args: Array[String]): Unit = {
    //Repl()
    SwingUtilities.invokeLater(new Runnable() {
      override def run(): Unit = {
        new EditorView().setVisible(true)
      }
    })
  }
}

package com.melvic.chi

import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env

import scala.annotation.tailrec

//noinspection SpellCheckingInspection
object Repl {
  type Print = String => Unit
  type ReadLine = String => String

  def start(print: Print, readLine: ReadLine)(implicit prefs: Preferences): Unit = {
    print(
      "Welcome to the Chi Repl. Write your propositions or function signatures below " +
        "and press enter to see the results"
    )
    implicit val env: Env = Env.default
    loop(print, readLine)
  }

  @tailrec
  def loop(print: Print, readLine: ReadLine)(implicit prefs: Preferences, env: Env): Unit =
    readLine("chi> ") match {
      case "exit" => print("Bye!")
      case ""     => loop(print, readLine)
      case input =>
        val newEnv: Env = Env.fetchAssumptions(input :: Nil)
        print(eval.generateAndShow(input))
        print("")
        loop(print, readLine)(prefs, newEnv)
    }
}

package com.melvic.chi

import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env

import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Repl {
  def start(implicit prefs: Preferences): Unit = {
    println(
      "Welcome to the Chi Repl. Write your propositions or function signatures below " +
        "and press enter to see the results"
    )
    implicit val env: Env = Env.default
    loop
  }

  @tailrec
  def loop(implicit prefs: Preferences, env: Env): Unit =
    readLine("chi> ") match {
      case "exit" => println("Bye!")
      case ""     => loop
      case input =>
        val newEnv: Env = Env.fetchAssumptions(input :: Nil)
        println(eval.generateAndShow(input))
        println()
        loop(prefs, newEnv)
    }
}

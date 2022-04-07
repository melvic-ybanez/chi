package com.melvic.chi

import com.melvic.chi.config.Preferences

import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Repl {
  def apply(implicit prefs: Preferences): Unit = {
    println("Welcome to the Chi Repl. Write your propositions or function signatures below " +
      "and press enter to see the results")
    loop
  }

  @tailrec
  def loop(implicit prefs: Preferences): Unit =
    readLine("chi> ") match {
      case "exit" => println("Bye!")
      case ""     => loop
      case input =>
        println(generateAndShow(input))
        println()
        loop
    }
}

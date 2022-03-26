package com.melvic.chi

import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Repl {
  def apply(): Unit = {
    println("Welcome to the Chi Repl. Write your propositions or function signatures below " +
      "and press enter to see the results")
    loop()
  }

  @tailrec
  def loop(): Unit =
    readLine("chi> ") match {
      case "exit" => println("Bye!")
      case ""     => loop()
      case input =>
        println(generateAndShow(input))
        println()
        loop()
    }
}

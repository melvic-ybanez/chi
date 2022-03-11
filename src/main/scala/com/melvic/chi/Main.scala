package com.melvic.chi

import com.melvic.chi.ast.Proposition.{Atom, Implication}
import com.melvic.chi.env.Environment
import com.melvic.chi.env.Environment.Environment

import scala.annotation.tailrec
import scala.io.StdIn.readLine

//noinspection SpellCheckingInspection
object Main {
  def main(args: Array[String]): Unit = {
    repl
  }

  @tailrec
  def repl(): Unit =
    readLine("chi> ") match {
      case "exit" => ()
      case "" => repl()
      case input =>
        println(generateAndShow(input))
        repl()
    }
}

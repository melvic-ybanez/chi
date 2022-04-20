package com.melvic.chi.output

import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.Atom
import com.melvic.chi.parsers.Language

sealed trait Fault

object Fault {
  final case class CannotProve(proposition: Proposition) extends Fault
  final case class ParseError(msg: String) extends Fault
  final case class UnknownPropositions(identifiers: List[Atom]) extends Fault

  def cannotProve(proposition: Proposition): Fault =
    CannotProve(proposition)

  def parseError(msg: String): Fault =
    ParseError(msg)

  def show(fault: Fault): String = {
    // For the mean time, let's just use Scala's syntax for the
    // error reporting of propositions. I mean, this project is
    // Scala-biased anyway.
    val display = Display.fromLanguage(Language.Scala, "")

    fault match {
      case CannotProve(proposition) =>
        val propositionString = display.showProposition(proposition)
        s"Can not prove the following proposition: $propositionString"
      case ParseError(msg) => s"Parse Error: $msg"
      case UnknownPropositions(identifiers) =>
        s"Unknown propositions: ${Utils.toCSV(identifiers.map(display.showProposition(_)))}"
    }
  }
}

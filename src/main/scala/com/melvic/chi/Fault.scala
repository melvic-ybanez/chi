package com.melvic.chi

import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.Atom

sealed trait Fault

object Fault {
  final case class CannotProve(proposition: Proposition) extends Fault
  final case class ParseError(msg: String) extends Fault
  final case class UnknownPropositions(identifiers: List[Atom]) extends Fault

  def cannotProve(proposition: Proposition): Fault =
    CannotProve(proposition)

  def parseError(msg: String): Fault =
    ParseError(msg)

  def show(fault: Fault): String =
    fault match {
      case CannotProve(proposition) =>
        val propositionString = Proposition.show(proposition)
        s"Can not prove the following proposition: $propositionString"
      case ParseError(msg) => s"Parse Error: $msg"
      case UnknownPropositions(identifiers) =>
        s"Unknown propositions: ${identifiers.map(Proposition.show(_)).mkString(", ")}"
    }
}

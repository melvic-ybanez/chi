package com.melvic.chi

import com.melvic.chi.ast.Proposition

sealed trait Fault

object Fault {
  final case class CannotProve(proposition: Proposition) extends Fault
  final case class ParseError(msg: String) extends Fault

  def cannotProve(proposition: Proposition): Fault =
    CannotProve(proposition)

  def parseError(msg: String): Fault =
    ParseError(msg)
}

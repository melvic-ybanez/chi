package com.melvic.chi

sealed trait Error

object Error {
  final case class CannotProve(proposition: Proposition) extends Error
  final case class ParseError(input: String) extends Error

  def cannotProve(proposition: Proposition): Error =
    CannotProve(proposition)

  def parseError(input: String): Error =
    ParseError(input)
}

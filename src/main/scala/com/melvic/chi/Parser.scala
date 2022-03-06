package com.melvic.chi

import com.melvic.chi.Proposition.{Atom, Conjunction, Disjunction, Implication}
import fastparse.NoWhitespace._
import fastparse._

object Parser {
  def atom[_: P]: P[Atom] = CharPred(_.isLetter).rep.!.map(Atom)

  def conjunction[_: P]: P[Conjunction] =
    P(openParen ~ proposition.rep(sep = ",", min = 0)./ ~ closeParen)
      .map(propositions => Conjunction(propositions.toList))

  def disjunction[_: P]: P[Disjunction] =
    P("Either[" ~ maybeSpaces ~ proposition ~ "," ~ proposition ~ maybeSpaces ~ "]")
      .map { case (left, right) => Disjunction(left, right) }

  def partial[_: P]: P[Proposition] =
    P(openParen.? ~ maybeSpaces ~ (atom | conjunction | disjunction) ~ maybeSpaces ~ closeParen.?)

  def arrow[_: P] = P(maybeSpaces ~ "=>" ~ maybeSpaces)

  def implication[_: P] = {
    P(partial ~ (arrow ~ partial).rep(0))
      .map {
        case (antecedent, consequent) =>
          consequent.foldLeft(antecedent) {
            case (antecedent, consequent) =>
              Implication(antecedent, consequent)
          }
      }
  }

  def proposition[_: P]: P[Proposition] = P(implication ~ End)

  def functionCode[_: P] = {
    def typeParams = atom.rep(sep = ",", min = 1)

    P(
      "def" ~ spaces(1) ~ name.! ~ "[" ~ maybeSpaces ~ typeParams ~ maybeSpaces ~ "]" ~ maybeSpaces ~ ":" ~ proposition
    ).map {
      case (name, typeParams, proposition) =>
        FunctionCode(name, typeParams.map(_.value).toList, proposition)
    }
  }

  def maybeSpaces[_: P] = spaces(0)

  def spaces[_: P](min: Int = 0) = P(CharsWhileIn(" \r\n", min))

  def openParen[_: P] = P(maybeSpaces ~ "(" ~ maybeSpaces)

  def closeParen[_: P] = P(maybeSpaces ~ ")" ~ maybeSpaces)

  // TODO: Might need to reduce restriction here
  def name[_: P] = P(CharsWhile(c => c.isLetter))

  def parseFunctionCode(code: String): Result[FunctionCode] =
    parse(code, functionCode(_)) match {
      case Parsed.Success(code, _) => Right(code)
      case failure @ Parsed.Failure(_, _, _) =>
        Left(Error.parseError(failure.msg))
    }
}

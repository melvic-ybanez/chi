package com.melvic.chi

import fastparse._
import NoWhitespace._
import com.melvic.chi.Proposition.{Atom, Conjunction, Disjunction, Implication}

object Parser {
  def atom[_: P] = AnyChar.rep.!.map(Atom)

  def conjunction[_: P] = P("(" ~ proposition.rep(sep = ",", min = 0)./ ~ ")")
    .map(propositions => Conjunction(propositions.toList))

  def disjunction[_: P] = P("Either[" ~ proposition ~ "," ~ proposition ~ "]")
    .map { case (left, right) => Disjunction(left, right) }

  def implication[_: P] = P(proposition ~ "=>" ~ proposition)
    .map { case (antecedent, consequent) => Implication(antecedent, consequent) }

  def proposition[_: P]: P[Proposition] = P(atom | conjunction | disjunction | implication)
}

package com.melvic.chi

sealed trait Proposition

object Proposition {
  final case class Atom(value: String) extends Proposition
  final case class Variable(name: String, proposition: Proposition) extends Proposition
  final case class Conjunction(left: Proposition, right: Proposition) extends Proposition
  final case class Disjunction(left: Proposition, right: Proposition) extends Proposition
  final case class Implication(antecedent: Proposition, consequent: Proposition) extends Proposition
}

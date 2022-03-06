package com.melvic.chi

sealed trait Proposition

object Proposition {
  final case class Atom(value: String) extends Proposition
  final case class Conjunction(components: List[Proposition]) extends Proposition
  final case class Disjunction(left: Proposition, right: Proposition) extends Proposition
  final case class Implication(antecedent: Proposition, consequent: Proposition) extends Proposition
}

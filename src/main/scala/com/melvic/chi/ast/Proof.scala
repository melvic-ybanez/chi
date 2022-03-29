package com.melvic.chi.ast

sealed trait Proof

//noinspection SpellCheckingInspection
object Proof {
  sealed trait TUnit extends Proof
  case object TUnit extends TUnit
  final case class Variable(name: String, proposition: Proposition) extends Proof
  final case class Conjunction(components: List[Proof]) extends Proof
  final case class PRight(proof: Proof) extends Proof
  final case class PLeft(proof: Proof) extends Proof

  /**
    * A proof that utilizes both components, unlike what [[PRight]] and [[PLeft]] are for.
    * Each component is a pair: the components name and the component itself.
    */
  final case class Disjunction(name: String, left: (String, Proof), right: (String, Proof)) extends Proof

  final case class Abstraction(domain: Proof, codomain: Proof) extends Proof
  final case class Application(functionName: String, params: List[Proof]) extends Proof
}

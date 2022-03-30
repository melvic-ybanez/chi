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

  /**
    * Function application. The first parameter is a proof by itself, rather than a function
    * name as a string, in order to support invokations of curried functions (e.g `f(a)(b)`)
    */
  final case class Application(function: Proof, params: List[Proof]) extends Proof
}

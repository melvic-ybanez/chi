package com.melvic.chi.ast

import com.melvic.chi.ast.Proposition.PUnit

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
    * A pair of abstractions. This is used when there are two ways to construct a proof
    * (e.g. Scala's Either pattern match)
    */
  final case class EitherCases(left: Abstraction, right: Abstraction) extends Proof

  final case class EitherMatch(name: String, disjunction: EitherCases) extends Proof

  final case class Abstraction(domain: Proof, codomain: Proof) extends Proof

  /**
    * Function application. The first parameter is a proof by itself, rather than a function
    * name as a string, in order to support invokations of curried functions (e.g `f(a)(b)`)
    */
  final case class Application(function: Proof, params: List[Proof]) extends Proof

  final case class Infix(left: Proof, right: Proof) extends Proof

  final case class Indexed(function: Proof, index: Int) extends Proof

  def atomicVariable(name: String): Variable =
    Variable(name, PUnit)

  def applyOne(function: Proof, arg: Proof): Application =
    Application(function, arg :: Nil)
}

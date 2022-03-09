package com.melvic.chi.ast

sealed trait Proof

//noinspection SpellCheckingInspection
object Proof {
  sealed trait TUnit                                                      extends Proof
  case object TUnit                                                       extends TUnit
  final case class Variable(name: String, proposition: Proposition)       extends Proof
  final case class Conjunction(components: List[Proof])                   extends Proof
  final case class PRight(proof: Proof)                                   extends Proof
  final case class PLeft(proof: Proof)                                    extends Proof
  final case class Abstraction(domain: Proof, codomain: Proof)            extends Proof
  final case class Application(functionName: String, params: List[Proof]) extends Proof

  def show(proof: Proof): String =
    proof match {
      case TUnit                             => "()"
      case Variable(name, _)                 => name
      case Conjunction(terms)                => "(" + terms.map(Proof.show).mkString(", ") + ")"
      case PRight(term)                      => s"Right(${Proof.show(term)})"
      case PLeft(term)                       => s"Left(${Proof.show(term)})"
      case Abstraction(params: Conjunction, codomain) =>
        s"{ case ${show(params)} =>\n    ${show(codomain)}\n  }"
      case Abstraction(domain, codomain)     => s"${Proof.show(domain)} => ${Proof.show(codomain)}"
      case Application(functionName, params) => s"$functionName(${params.map(Proof.show).mkString(", ")})"
    }
}

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
  final case class Abstraction(domain: Proof, codomain: Proof) extends Proof
  final case class Application(functionName: String, params: List[Proof]) extends Proof

  def show(proof: Proof, level: Option[Int]): String = {
    val singleIndent = "  "
    val indent = level.map(singleIndent * _).getOrElse("")
    val nextLine = level.map(_ => "\n").getOrElse("")
    val nextLevel = level.map(_ + 1)
    val proofString = proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case Conjunction(terms) =>
        val termsString = terms
          .map(Proof.show(_, nextLevel))
          .mkString(", " + nextLine)
        "(" + nextLine + termsString + nextLine + indent + ")"
      case PRight(term) => s"Right(${Proof.show(term, None)})"
      case PLeft(term)  => s"Left(${Proof.show(term, None)})"
      case Abstraction(params: Conjunction, codomain) =>
        val bodyIndent = if (indent.nonEmpty) indent + singleIndent else singleIndent * 2
        s"{ case ${show(params, None)} =>\n$bodyIndent${show(codomain, None)}\n  }"
      case Abstraction(domain, codomain) =>
        s"${Proof.show(domain, None)} => $nextLine${Proof.show(codomain, nextLevel)}"
      case Application(functionName, params) =>
        s"$functionName($nextLine${params.map(Proof.show(_, nextLevel)).mkString(", " + nextLine)}$nextLine$indent)"
    }
    s"$indent$proofString"
  }
}

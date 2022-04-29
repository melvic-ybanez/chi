package com.melvic.chi.output

import com.melvic.chi.ast.Proof.{Conjunction => _, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowScala extends Show { show =>
  override def signature(signature: Signature, split: Boolean = false): String = {
    val typeParamsString = signature.typeParams match {
      case Nil        => ""
      case typeParams => s"[${Show.toCSV(typeParams)}]"
    }
    val paramsString = signature.params match {
      case Nil => ""
      case params => paramsList(params, split)
    }

    s"def ${signature.name}$typeParamsString$paramsString: ${show.proposition(signature.returnType)}"
  }

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + Show.toCSV(components.map(show.proposition)) + ")"
      case Disjunction(left, right)            => s"Either[${show.proposition(left)}, ${show.proposition(right)}]"
      case Implication(impl: Implication, out) => s"(${show.proposition(impl)}) => ${show.proposition(out)}"
      case Implication(antecedent, consequent) =>
        s"${show.proposition(antecedent)} => ${show.proposition(consequent)}"
    }

  override def indentWidth = 2

  //noinspection SpellCheckingInspection
  override def proofWithLevel(proof: Proof, level: Option[Int]) = {
    val indent = this.indent(level)
    val nextLine = level.map(_ => "\n").getOrElse("")
    val nextLevel = level.map(_ + 1)
    val bodyIndent = this.bodyIndent(level)
    val endCurlyIndent = if (indent.nonEmpty) indent else singleIndent

    val proofString = proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case Proof.Conjunction(terms) =>
        val termsString = terms
          .map(proofWithLevel(_, nextLevel))
          .mkString(", " + nextLine)
        "(" + nextLine + termsString + nextLine + indent + ")"
      case PRight(term) => s"Right(${show.proof(term)})"
      case PLeft(term)  => s"Left(${show.proof(term)})"
      case EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut)) =>
        val leftCase = s"case Left(${show.proof(leftIn)}) => ${show.proof(leftOut)}"
        val rightCase = s"case Right(${show.proof(rightIn)}) => ${show.proof(rightOut)}"
        s"{\n$bodyIndent$leftCase\n$bodyIndent$rightCase\n$endCurlyIndent}"
      case Match(name, term: Proof) =>
        s"$name match ${proofWithLevel(term, level)}"
      case Abstraction(params: Proof.Conjunction, codomain) =>
        s"{ case ${show.proof(params)} =>\n$bodyIndent${show.proof(codomain)}\n$endCurlyIndent}"
      case Abstraction(domain, codomain) =>
        s"${show.proof(domain)} => $nextLine${proofWithLevel(codomain, nextLevel)}"
      case Application(function, params) =>
        val functionString = show.proof(function)
        s"$functionString($nextLine${params.map(proofWithLevel(_, nextLevel)).mkString(", " + nextLine)}$nextLine$indent)"
      case Infix(left, right) =>
        s"${show.proof(left)}.${show.proof(right)}"
      case Indexed(proof, index) => s"${show.proof(proof)}._$index"
    }

    s"$indent$proofString"
  }

  override def definition(signature: String, body: String, pretty: Boolean) = {
    val prettyBody = if (pretty) body else "  " + body
    s"$signature =\n$prettyBody"
  }
}

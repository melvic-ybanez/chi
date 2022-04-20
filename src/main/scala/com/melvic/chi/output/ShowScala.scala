package com.melvic.chi.output

import com.melvic.chi.ast.Proof.{Conjunction => _, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowScala extends Display {
  override def showSignature(signature: Signature, split: Boolean = false): String = {
    val typeParamsString = signature.typeParams match {
      case Nil        => ""
      case typeParams => s"[${Utils.toCSV(typeParams)}]"
    }
    val paramsString = signature.params match {
      case Nil => ""
      case params =>
        val vars = params.map {
          case Variable(name, proposition) =>
            s"$name: ${showProposition(proposition)}"
        }
        s"(${Utils.splitParams(vars, split)})"
    }

    s"def ${signature.name}$typeParamsString$paramsString: ${showProposition(signature.returnType)}"
  }

  override def showProposition(proposition: Proposition) =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + Utils.toCSV(components.map(showProposition)) + ")"
      case Disjunction(left, right)            => s"Either[${showProposition(left)}, ${showProposition(right)}]"
      case Implication(impl: Implication, out) => s"(${showProposition(impl)}) => ${showProposition(out)}"
      case Implication(antecedent, consequent) =>
        s"${showProposition(antecedent)} => ${showProposition(consequent)}"
    }

  override def numberOfSpacesForIndent = 2

  //noinspection SpellCheckingInspection
  override def showProofWithLevel(proof: Proof, level: Option[Int]) = {
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
          .map(showProofWithLevel(_, nextLevel))
          .mkString(", " + nextLine)
        "(" + nextLine + termsString + nextLine + indent + ")"
      case PRight(term) => s"Right(${showProof(term)})"
      case PLeft(term)  => s"Left(${showProof(term)})"
      case EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut)) =>
        val leftCase = s"case Left(${showProof(leftIn)}) => ${showProof(leftOut)}"
        val rightCase = s"case Right(${showProof(rightIn)}) => ${showProof(rightOut)}"
        s"{\n$bodyIndent$leftCase\n$bodyIndent$rightCase\n$endCurlyIndent}"
      case EitherMatch(name, term: EitherCases) =>
        s"$name match ${showProofWithLevel(term, level)}"
      case Abstraction(params: Proof.Conjunction, codomain) =>
        s"{ case ${showProof(params)} =>\n$bodyIndent${showProof(codomain)}\n$endCurlyIndent}"
      case Abstraction(domain, codomain) =>
        s"${showProof(domain)} => $nextLine${showProofWithLevel(codomain, nextLevel)}"
      case Application(function, params) =>
        val functionString = showProof(function)
        s"$functionString($nextLine${params.map(showProofWithLevel(_, nextLevel)).mkString(", " + nextLine)}$nextLine$indent)"
      case Infix(left, right) =>
        s"${showProof(left)}.${showProof(right)}"
      case Attribute(proof, name) => s"${showProof(proof)}.$name"
    }

    s"$indent$proofString"
  }

  override def showDefinition(signature: String, body: String, pretty: Boolean) = {
    val prettyBody = if (pretty) body else "  " + body
    s"$signature =\n$prettyBody"
  }
}

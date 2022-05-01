package com.melvic.chi.output

import com.melvic.chi.ast.Proof.{Conjunction => _, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.config.Preferences

class ShowScala(implicit val prefs: Preferences) extends Show { show =>
  def signature: SignatureLayout = signatureWithSplit(false)

  def body(proof: Proof)(implicit config: (Int, Boolean, Int, Int)): String = {
    val (tupleIndent, nestLastConsequentOnly, consequentIndent, applyIndent) = config
    proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case Proof.Conjunction(terms) =>
        val tupleLine = if (tupleIndent == 0) "" else line
        val termsString = terms
          .map(body(_))
          .mkString(", " + tupleLine)
        "(" + nestWithIndent(tupleLine + termsString, tupleIndent) + tupleLine + ")"
      case PRight(term) => s"Right(${show.body(term)})"
      case PLeft(term)  => s"Left(${show.body(term)})"
      case EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut)) =>
        val leftCase = s"case Left(${show.body(leftIn)}) => ${show.body(leftOut)}"
        val rightCase = s"case Right(${show.body(rightIn)}) => ${show.body(rightOut)}"
        "{" + nest(line + leftCase + line + rightCase) + line + "}"
      case Match(name, term: Proof) =>
        s"$name match ${show.body(term)}"
      case Abstraction(in, out: Abstraction) if nestLastConsequentOnly =>
        s"${show.body(in)} => ${show.body(out)}"
      case Abstraction(params: Proof.Conjunction, out) =>
        val right = nest(line + show.body(out))
        s"{ case ${show.body(params)} =>$right$line}"
      case Abstraction(domain, codomain) =>
        val line = if (consequentIndent == 0) "" else show.line
        val out = nestWithIndent(line + show.body(codomain), consequentIndent)
        s"${show.body(domain)} => $out$line"
      case Application(function, params) =>
        val functionString = show.body(function)
        val applyLine = if (applyIndent == 0) "" else line
        val paramsString =
          nestWithIndent(applyLine + params.map(show.body).mkString(", " + applyLine), applyIndent)
        s"$functionString($paramsString$applyLine)"
      case Infix(left, right) =>
        s"${show.body(left)}.${show.body(right)}"
      case Indexed(proof, index) => s"${show.body(proof)}._$index"
    }
  }

  override def bodyLayouts = List(
    oneLine,
    lastConsequentOnNextLine,
    oneLinePerAntecedent,
    oneLinePerTupleComponent,
    splitApplication
  )

  def oneLine: ProofLayout = show.body(_)(0, false, 0, 0)

  override def prettySignature: SignatureLayout = signatureWithSplit(true)

  def lastConsequentOnNextLine: ProofLayout = show.body(_)(0, true, 0, 0)

  def oneLinePerAntecedent: ProofLayout = show.body(_)(0, true, indentWidth, 0)

  def oneLinePerTupleComponent: ProofLayout = show.body(_)(indentWidth, true, indentWidth, 0)

  def splitApplication: ProofLayout = show.body(_)(indentWidth, true, indentWidth, indentWidth)

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + Show.toCSV(components.map(show.proposition)) + ")"
      case Disjunction(left, right)            => s"Either[${show.proposition(left)}, ${show.proposition(right)}]"
      case Implication(impl: Implication, out) => s"(${show.proposition(impl)}) => ${show.proposition(out)}"
      case Implication(antecedent, consequent) =>
        s"${show.proposition(antecedent)} => ${show.proposition(consequent)}"
    }

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, typeParams, params, returnType) =>
      val typeParamsString = typeParams match {
        case Nil        => ""
        case typeParams => s"[${Show.toCSV(typeParams)}]"
      }
      val paramsString = params match {
        case Nil    => ""
        case params => paramsList(params, split)
      }

      s"def $name$typeParamsString$paramsString: ${show.proposition(returnType)} ="
  }

  override def indentWidth = 2

  override def makeDef(signature: String, body: String) =
    signature + nest(line + body)
}

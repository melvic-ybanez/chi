package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Conjunction => _, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.show.ShowScala._
import com.melvic.chi.output.{ParamsInParens, ProofLayout, SignatureLayout}

class ShowScala(implicit val prefs: Preferences) extends Show with ScalaLike with ParamsInParens { show =>
  def proof(proof: Proof)(implicit formats: List[Format]): String =
    proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case Proof.Conjunction(terms) =>
        val (tupleLine, tupleIndent) = formatParams(Format.has(FormatTuple))
        val termsString = terms
          .map(show.proof(_))
          .mkString(", " + tupleLine)
        "(" + nestWithIndent(tupleLine + termsString, tupleIndent) + tupleLine + ")"
      case PRight(term) => s"Right(${show.proof(term)})"
      case PLeft(term)  => s"Left(${show.proof(term)})"
      case EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut)) =>
        val leftCase = s"case Left(${show.proof(leftIn)}) => ${show.proof(leftOut)}"
        val rightCase = s"case Right(${show.proof(rightIn)}) => ${show.proof(rightOut)}"
        "{" + nest(line + leftCase + line + rightCase) + line + "}"
      case Match(name, term: Proof) =>
        s"$name match ${show.proof(term)}"
      case Abstraction(in, out: Abstraction) if Format.has(FormatRightMostLambda) =>
        s"${show.proof(in)} => ${show.proof(out)}"
      case Abstraction(params: Proof.Conjunction, out) =>
        val right = nest(line + show.proof(out))
        s"{ case ${show.proof(params)} =>$right$line}"
      case Abstraction(domain, codomain) =>
        val (line, functionIndent) = formatParams(Format.has(FormatLambda))
        val out = nestWithIndent(line + show.proof(codomain), functionIndent)
        s"${show.proof(domain)} => $out$line"
      case Application(function, params) =>
        val functionString = show.proof(function)
        val (applyLine, applyIndent) = formatParams(Format.has(FormatApplication))
        val paramsString =
          nestWithIndent(applyLine + params.map(show.proof).mkString(", " + applyLine), applyIndent)
        s"$functionString($paramsString$applyLine)"
      case Infix(left, right) =>
        s"${show.proof(left)}.${show.proof(right)}"
      case Indexed(proof, index) => s"${show.proof(proof)}._$index"
    }

  override def bodyLayouts = List(
    oneLine,
    withFormattedRightMostLambda,
    withFormattedLambda,
    withFormattedTuple,
    withFormattedApplication
  )

  def oneLine: ProofLayout = show.proof(_)(Nil)

  def withFormattedRightMostLambda: ProofLayout = show.proof(_)(FormatRightMostLambda :: Nil)

  def withFormattedLambda: ProofLayout =
    show.proof(_)(FormatLambda :: FormatRightMostLambda :: Nil)

  def withFormattedTuple: ProofLayout =
    show.proof(_)(FormatTuple :: FormatLambda :: FormatRightMostLambda :: Nil)

  def withFormattedApplication: ProofLayout =
    show.proof(_)(FormatApplication :: FormatLambda :: FormatRightMostLambda :: FormatTuple :: Nil)

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)              => value
      case Conjunction(components)  => "(" + propositionCSV(components) + ")"
      case Disjunction(left, right) => s"Either[${show.proposition(left)}, ${show.proposition(right)}]"
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
        case params => paramList(params, split)
      }

      s"def $name$typeParamsString$paramsString: ${show.proposition(returnType)} ="
  }

  override def indentWidth = 2

  override def makeDef(signature: String, body: String) =
    signature + nest(line + body)
}

object ShowScala {
  sealed trait Format

  case object FormatTuple extends Format
  case object FormatRightMostLambda extends Format
  case object FormatLambda extends Format
  case object FormatApplication extends Format

  object Format {
    def has(format: Format)(implicit formats: List[Format]): Boolean =
      formats.contains(format)
  }
}

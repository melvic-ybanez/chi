package com.melvic.chi.eval

import com.melvic.chi.ast.Proposition.{Atom, Identifier, PUnit}
import com.melvic.chi.ast.{AssertIso, Definition, Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.output.Fault.UnknownPropositions
import com.melvic.chi.output.Result.Result
import com.melvic.chi.output.{IsoResult, Result}
import com.melvic.chi.parsers
import com.melvic.chi.parsers.{IsomorphismParser, Language}

import scala.annotation.tailrec

object Generate {
  def codeFromSignature(signature: Signature, language: Language)(
      implicit prefs: Preferences
  ): Result[Definition] = {
    val Signature(name, typeParams, params, proposition) = signature

    implicit val localFnName: String = name

    val unknownTypes = Proposition.filter(proposition) {
      case PUnit => false
      case atom @ Atom(value) =>
        !typeParams.map(Identifier).contains(atom) && !Language.builtInTypes(language).contains(value)
    }

    if (unknownTypes.nonEmpty) Result.fail(UnknownPropositions(unknownTypes))
    else
      Prover
        .proveProposition(proposition)(Env.fromListWithDefault(params))
        .map(Transform.from(_, language))
        .map(Definition(signature, _, language))
  }

  def codeFromSignatureString(signature: String)(implicit prefs: Preferences): Result[Definition] =
    parsers
      .parseLanguageSignature(signature)
      .flatMap {
        case (signature, lang) => codeFromSignature(signature, lang)
      }

  def assertIso(signature: String): Result[IsoResult] =
    IsomorphismParser.parseIso(signature).map {
      case assert @ AssertIso(s, s1) => Signature.isomorphic(s, s1)
    }

  def allToLines(lines: List[String])(implicit preferences: Preferences): List[String] = {
    val evaluate: Evaluate = if (Preferences.showOutputInfo) generateAndShowWithInfo else generateAndShowCode
    val definitions = parsers.Utils.removeComments(lines)
      .map(_.trim) // we need to trim again to remove extra spaces between a definition and a comment
      .filter(_.nonEmpty)

    @tailrec
    def recurse(outputs: List[String], definitions: List[String]): List[String] =
      definitions match {
        case Nil => outputs
        case _ =>
          val (output, rest) = Generate.fromLines(definitions, evaluate)
          recurse(output :: outputs, rest)
      }

    recurse(Nil, definitions).reverse
  }

  def allToString(lines: List[String])(implicit preferences: Preferences): String =
    allToLines(lines).mkString("\n\n")

  def fromLines(lines: List[String], evaluate: Evaluate): (String, List[String]) = {
    @tailrec
    def recurse(partial: String, restOrLines: List[String]): (String, List[String]) =
      restOrLines match {
        case Nil => (evaluate(partial), lines.tail)
        case line :: rest =>
          val signature = partial + " " + line
          if (parsers.validInput(signature)) (evaluate(signature), rest)
          else recurse(signature, rest)
      }

    recurse("", lines)
  }
}

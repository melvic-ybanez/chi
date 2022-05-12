package com.melvic.chi.eval

import com.melvic.chi.ast.Proposition.{Atom, Identifier, PUnit}
import com.melvic.chi.ast._
import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.output.Fault.UnknownPropositions
import com.melvic.chi.output.Result
import com.melvic.chi.output.Result.Result
import com.melvic.chi.parsers
import com.melvic.chi.parsers.Language

import scala.annotation.tailrec

object Generate {
  def codeFromSignature(signature: Signature, language: Language)(
      implicit prefs: Preferences,
      env: Env
  ): Result[Definition] = {
    val Signature(name, typeParams, params, proposition) = signature

    implicit val localFnName: String = name

    val unknownTypes = Proposition.filter(Signature.fullProposition(signature)) {
      case PUnit => false
      case atom @ Atom(value) =>
        !typeParams.map(Identifier).contains(atom) && !language.builtInTypes.contains(value)
    }

    if (unknownTypes.nonEmpty) Result.fail(UnknownPropositions(unknownTypes))
    else
      Prove
        .proposition(proposition)(Env.addPoofs(params))
        .map(Transform.from(_, language))
        .map(Definition(signature, _, language))
  }

  def codeFromSignatureString(signature: String)(implicit prefs: Preferences, env: Env): Result[Definition] =
    parsers
      .parseLanguageSignature(signature)
      .flatMap {
        case (signature, lang) => codeFromSignature(signature, lang)
      }

  def allToLines(lines: List[String])(implicit preferences: Preferences): List[String] = {
    val definitions = parsers.Utils
      .removeComments(lines)
      .map(_.trim) // we need to trim again to remove extra spaces between a definition and a comment
      .filter(_.nonEmpty)

    implicit val env: Env = Env.fetchAssumptions(definitions)(Env.default)

    @tailrec
    def recurse(outputs: List[String], definitions: List[String]): List[String] =
      definitions match {
        case Nil => outputs
        case _ =>
          val (output, rest) = Generate.fromLines(definitions, generateAndShow)
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
          // Note: this is not very efficient as we are doing the parsing twice
          // here with the validation and the evaluation.
          // Perhaps I didn't think this through :(
          if (parsers.validateInput(signature)) (evaluate(signature), rest)
          else recurse(signature, rest)
      }

    recurse("", lines)
  }
}

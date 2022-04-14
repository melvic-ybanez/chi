package com.melvic.chi.eval

import com.melvic.chi.ast.Proposition.{Atom, Identifier, PUnit}
import com.melvic.chi.ast.{Definition, AssertIso, Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.out.Fault.UnknownPropositions
import com.melvic.chi.out.Result
import com.melvic.chi.out.Result.Result
import com.melvic.chi.parsers.{IsomorphismParser, JavaParser, Language, ScalaParser}

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
    JavaParser
      .parseSignature(signature)
      .orElse(ScalaParser.parseSignature(signature))
      .flatMap {
        case (signature, lang) => codeFromSignature(signature, lang)
      }

  def assertIso(signature: String): Result[(AssertIso, Boolean)] =
    IsomorphismParser.parseIso(signature).map {
      case assert @ AssertIso(s, s1) => (assert, Proposition.isomorphic(s.proposition, s1.proposition))
    }
}

package com.melvic.chi.eval

import com.melvic.chi.ast.Proposition.{Atom, Identifier, PUnit}
import com.melvic.chi.ast.{Definition, Proposition, Signature}
import com.melvic.chi.env.Env
import com.melvic.chi.out.Fault.UnknownPropositions
import com.melvic.chi.out.Result
import com.melvic.chi.out.Result.Result
import com.melvic.chi.parsers.{JavaParser, Language, ScalaParser}

object Generate {
  def fromSignature(signature: Signature, language: Language): Result[Definition] = {
    val Signature(name, typeParams, params, proposition) = signature

    val unknownTypes = Proposition.filter(proposition) {
      case PUnit => false
      case atom @ Atom(value) =>
        !typeParams.map(Identifier).contains(atom) && !Language.builtInTypes(language).contains(value)
    }

    if (unknownTypes.nonEmpty) Result.fail(UnknownPropositions(unknownTypes))
    else
      Prover
        .proveProposition(proposition)(Env.fromListWithDefault(params))
        .map(Definition(signature, _, language))
  }

  def fromSignatureString(functionCode: String): Result[Definition] =
    ScalaParser
      .parseSignature(functionCode)
      .orElse(JavaParser.parseSignature(functionCode))
      .flatMap {
        case (signature, lang) => fromSignature(signature, lang)
      }
}

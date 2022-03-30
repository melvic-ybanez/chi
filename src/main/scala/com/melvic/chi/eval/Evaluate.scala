package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{Application, PLeft, PRight, TUnit, Variable}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Definition, Proof, Proposition, Signature}
import com.melvic.chi.env.Env
import com.melvic.chi.out.Fault.UnknownPropositions
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}
import com.melvic.chi.parsers.{JavaParser, Language, ScalaParser}

object Evaluate {
  //noinspection SpellCheckingInspection
  def fromProposition(proposition: Proposition)(implicit env: Env): Result[Proof] =
    proposition match {
      case PUnit                   => Result.success(TUnit)
      case atom: Atom              => deduce(atom)
      case Conjunction(components) => Rule.conjunctionIntroduction(components)
      case Disjunction(left, right) =>
        Evaluate
          .fromProposition(left)
          .map(PLeft)
          .orElse(Evaluate.fromProposition(right).map(PRight))
      case Implication(antecedent, consequent) => Rule.implicationIntroduction(antecedent, consequent)
    }

  def fromSignature(signature: Signature, language: Language): Result[Definition] = {
    val Signature(name, typeParams, params, proposition) = signature

    val unknownTypes = Proposition.filter(proposition) {
      case PUnit => false
      case atom =>
        !typeParams.map(Identifier).contains(atom)
    }

    if (unknownTypes.nonEmpty) Result.fail(UnknownPropositions(unknownTypes))
    else
      Evaluate
        .fromProposition(proposition)(Env.fromListWithDefault(params))
        .map(Definition(signature, _, language))
  }

  def signatureString(functionCode: String): Result[Definition] =
    ScalaParser
      .parseSignature(functionCode)
      .orElse(JavaParser.parseSignature(functionCode))
      .flatMap {
        case (signature, lang) => Evaluate.fromSignature(signature, lang)
      }

  def deduce(atom: Atom)(implicit env: Env): Result[Proof] =
    Rule
      .assumption(atom)
      .orElse(deduceImplication(atom))
      .orElse(deduceConjunction(atom))

  def deduceImplication(atom: Atom)(implicit env: Env): Result[Proof] = {
    val implicationOpt = Env.filterByConsequent(atom).headOption
    implicationOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap {
        case variable @ Variable(_, Implication(antecedent, consequent)) =>
          val newEnv = Env.without(variable)

          def recurse(function: Proof, in: Proposition, out: Proposition): Result[Proof] =
            Evaluate.fromProposition(in)(newEnv)
              .map(Rule.implicationElimination(function, _))
              .flatMap { application =>
                out match {
                  case `atom` => Result.success(application)
                  case Implication(newIn, newOut) => recurse(application, newIn, newOut)
                }
              }

          recurse(variable, antecedent, consequent).orElse(deduce(atom)(newEnv))
      }
  }

  def deduceConjunction(atom: Atom)(implicit env: Env): Result[Proof] = {
    val disjunctionOpt = Env.find {
      case Variable(_, _: Disjunction) => true
    }
    disjunctionOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap {
        case variable @ Variable(name, disjunction: Disjunction) =>
          Rule.disjunctionElimination(name, disjunction, atom)(Env.without(variable))
      }
  }
}

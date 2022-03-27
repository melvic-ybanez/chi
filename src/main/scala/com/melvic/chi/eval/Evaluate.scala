package com.melvic.chi.eval

import com.melvic.chi.Fault.UnknownPropositions
import com.melvic.chi.ast.Proof._
import com.melvic.chi.ast.Proposition.{Conjunction => TConjunction, _}
import com.melvic.chi.ast.{Definition, Proof, Proposition, Signature}
import com.melvic.chi.env.Environment
import com.melvic.chi.{Fault, Parser, Result}

object Evaluate {
  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Environment): Result[Proof] =
    proposition match {
      case PUnit                    => Result.success(TUnit)
      case atom: Atom               => deduce(atom)
      case TConjunction(components) => Rule.conjunctionIntroduction(components)
      case Disjunction(left, right) =>
        Evaluate
          .proposition(left)
          .map(PLeft)
          .orElse(Evaluate.proposition(right).map(PRight))
      case Implication(antecedent, consequent) => Rule.implicationIntroduction(antecedent, consequent)
    }

  def signature(signature: Signature): Result[Definition] = {
    val Signature(name, typeParams, params, proposition) = signature

    val unknownTypes = Proposition.filter(proposition) {
      case PUnit => false
      case atom =>
        !typeParams.map(Identifier).contains(atom)
    }

    if (unknownTypes.nonEmpty) Result.fail(UnknownPropositions(unknownTypes))
    else
      Evaluate
        .proposition(proposition)(Environment.fromListWithDefault(params))
        .map(Definition(signature, _))
  }

  def signatureString(functionCode: String): Result[Definition] =
    Parser.parseSignature(functionCode).flatMap(Evaluate.signature)

  def deduce(atom: Atom)(implicit env: Environment): Result[Proof] =
    Rule
      .assumption(atom)
      .orElse {
        val implicationOpt = Environment.findAssumption {
          case Variable(_, Implication(_, `atom`)) => true
        }
        implicationOpt
          .toRight(Fault.cannotProve(atom))
          .flatMap {
            case variable @ Variable(varName, impl: Implication) =>
              val newEnv = Environment.discharge(variable)
              Rule.implicationElimination(varName, impl)(newEnv).orElse(deduce(atom)(newEnv))
          }
      }
}

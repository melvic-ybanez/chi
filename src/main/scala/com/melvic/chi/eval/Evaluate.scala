package com.melvic.chi.eval

import com.melvic.chi.Parser
import com.melvic.chi.ast.Proof._
import com.melvic.chi.ast.Proposition.{Conjunction => TConjunction, _}
import com.melvic.chi.ast.{Definition, Proof, Proposition, Signature}
import com.melvic.chi.env.Env
import com.melvic.chi.out.Fault.UnknownPropositions
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}

object Evaluate {
  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Env): Result[Proof] =
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
        .proposition(proposition)(Env.fromListWithDefault(params))
        .map(Definition(signature, _))
  }

  def signatureString(functionCode: String): Result[Definition] =
    Parser.parseSignature(functionCode).flatMap(Evaluate.signature)

  def deduce(atom: Atom)(implicit env: Env): Result[Proof] =
    Rule
      .assumption(atom)
      .orElse {
        val implicationOpt = Env.filterByConsequent(atom).headOption
        implicationOpt
          .toRight(Fault.cannotProve(atom))
          .flatMap {
            case variable @ Variable(functionName, Implication(antecedent, _)) =>
              val newEnv = Env.without(variable)
              Evaluate
                .proposition(antecedent)(newEnv)
                .map(Rule.implicationElimination(functionName, _))
                .orElse(deduce(atom)(newEnv))
          }
      }
}

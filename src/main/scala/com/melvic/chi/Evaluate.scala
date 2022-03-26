package com.melvic.chi

import com.melvic.chi.Fault.UnknownPropositions
import com.melvic.chi.ast.Proof.{Conjunction => _, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Definition, Proof, Proposition, Signature}
import com.melvic.chi.env.Environment
import com.melvic.chi.env.Environment.Environment

object Evaluate {
  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Environment): Result[Proof] =
    proposition match {
      case PUnit => Result.success(TUnit)
      case atom: Atom =>
        Environment
          .findAtom(atom)
          .toRight(Fault.cannotProve(atom))
          .flatMap {
            case variable @ Variable(name, `atom`) => Result.success(variable)
            case variable @ Variable(f, Implication(antecedent, _)) =>
              Evaluate.proposition(antecedent)(env.filterNot(_ == variable)).map {
                case TUnit                    => Application(f, Nil)
                case Proof.Conjunction(terms) => Application(f, terms)
                case param                    => Application(f, List(param))
              }
          }
      case Conjunction(components) => conjunctionIntroduction(components)
      case Disjunction(left, right) =>
        Evaluate
          .proposition(left)
          .map(PLeft)
          .orElse(Evaluate.proposition(right).map(PRight))
      case Implication(antecedent, consequent) =>
        val (term, newEnv) = Environment.register(antecedent)
        Evaluate.proposition(consequent)(newEnv).map(Abstraction(term, _))
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
        .proposition(proposition)(Environment.fromList(params))
        .map(Definition(signature, _))
  }

  def signatureString(functionCode: String): Result[Definition] =
    Parser.parseSignature(functionCode).flatMap(Evaluate.signature)

  /**
    * Base on the the following rule for &-Introduction from propositional logic:
    *   A B
    * ------- (&-I)
    *  A & B
    * That is, if we assume proofs for A and B, we can construct a proof for their conjunction.
    * (But of course in our case, we are not limited to two components.
    */
  def conjunctionIntroduction(components: List[Proposition])(implicit env: Environment) = {
    def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
      components match {
        case Nil => Result.success(result)
        case component :: rest =>
          Evaluate.proposition(component).flatMap(proof => recurse(proof :: result, rest))
      }

    val evaluatedComponents = recurse(Nil, components)
    evaluatedComponents.map(Proof.Conjunction)
  }
}

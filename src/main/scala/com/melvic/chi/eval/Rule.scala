package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{Abstraction, Application, TUnit, Variable}
import com.melvic.chi.ast.Proposition.{Atom, Implication}
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Environment
import com.melvic.chi.{Fault, Result}

object Rule {

  /**
    * The assumption rule from propositional logic
    */
  def assumption(atom: Atom)(implicit env: Environment): Result[Proof] =
    Environment
      .findAssumption {
        case Variable(_, `atom`) => true
      }
      .toRight(Fault.cannotProve(atom))

  /**
    * Based on the the following rule for &-Introduction from propositional logic:
    *   A B
    * ------- (&-I)
    *  A & B
    * That is, if we assume proofs for A and B, we can construct a proof for their conjunction.
    * (But of course in our case, we are not limited to two components.
    */
  def conjunctionIntroduction(components: List[Proposition])(implicit env: Environment): Result[Proof] = {
    def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
      components match {
        case Nil => Result.success(result)
        case component :: rest =>
          Evaluate.proposition(component).flatMap(proof => recurse(proof :: result, rest))
      }

    val evaluatedComponents = recurse(Nil, components)
    evaluatedComponents.map(components => Proof.Conjunction(components.reverse))
  }

  def implicationIntroduction(antecedent: Proposition, consequent: Proposition)(
      implicit env: Environment
  ): Result[Proof] = {
    val (term, newEnv) = Environment.register(antecedent)
    Evaluate.proposition(consequent)(newEnv).map(Abstraction(term, _))
  }

  def implicationElimination(varName: String, implication: Implication)(
      implicit env: Environment
  ): Result[Proof] = {
    val Implication(antecedent, _) = implication
    Evaluate
      .proposition(antecedent)
      .map {
        case TUnit                    => Application(varName, Nil)
        case Proof.Conjunction(terms) => Application(varName, terms)
        case param                    => Application(varName, List(param))
      }
  }
}

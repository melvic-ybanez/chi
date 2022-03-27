package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{Abstraction, Application, TUnit, Variable}
import com.melvic.chi.ast.Proposition.{Atom, Disjunction}
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Env
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}

object Rule {

  /**
    * The assumption rule from propositional logic
    */
  def assumption(atom: Atom)(implicit env: Env): Result[Proof] =
    Env
      .find {
        case Variable(_, `atom`) => true
      }
      .toRight(Fault.cannotProve(atom))

  /**
    * Finds proof for the components and use them to construct the proof for the
    * conjunction.
    * This is based on the the following rule for &-Introduction from propositional logic:
    *   A B
    * ------- (&-I)
    *  A & B
    * That is, if we assume proofs for A and B, we can construct a proof for their conjunction.
    * (But of course in our case, we are not limited to two components.
    */
  def conjunctionIntroduction(components: List[Proposition])(implicit env: Env): Result[Proof] = {
    def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
      components match {
        case Nil => Result.success(result)
        case component :: rest =>
          Evaluate.proposition(component).flatMap(proof => recurse(proof :: result, rest))
      }

    val evaluatedComponents = recurse(Nil, components)
    evaluatedComponents.map(components => Proof.Conjunction(components.reverse))
  }

  /**
    * This is based on the =>-Introduction from propositional logic:
    *   [A]
    *    .
    *    .
    *    B
    * ------- (=>-I)
    *    C
    *
    * The function also adds a discharged assumption to the environment.
    */
  def implicationIntroduction(antecedent: Proposition, consequent: Proposition)(
      implicit env: Env
  ): Result[Proof] = {
    val (term, newEnv) = Env.register(antecedent)
    Evaluate.proposition(consequent)(newEnv).map(Abstraction(term, _))
  }

  /**
    * Provides proof for the consequent of the implication. This is based on the
    * following rule for implication-elimination:
    *   A
    *   A => B
    * ---------- (=>-E)
    *     B
    * In programming, it means applying the function to the argument.
    */
  def implicationElimination(functionName: String, argument: Proof): Proof =
    argument match {
      case TUnit                    => Application(functionName, Nil)
      case Proof.Conjunction(terms) => Application(functionName, terms)
      case param                    => Application(functionName, List(param))
    }

  /**
    * If A => C and B => C, then (A|B) => C. This is according to the following
    * disjunction-elimination rule:
    *        [A] [B]
    *         .   .
    *         .   .
    *    A|B  C   C
    *  --------------- (|-E)
    *         C
    */
  def disjunctionElimination(name: String, disjunction: Disjunction, consequent: Proposition)(
      implicit env: Env
  ): Result[Proof] = {
    def proveComponent(component: Proposition): Result[(Proof, Proof)] = {
      val (proofId, newEnv) = Env.register(component)
      Evaluate.proposition(consequent)(newEnv).map((proofId, _))
    }

    val Disjunction(left, right) = disjunction
    proveComponent(left).flatMap {
      case (Variable(leftName, _), leftProof) =>
        proveComponent(right).flatMap {
          case (Variable(rightName, _), rightProof) =>
            Result.success(Proof.Disjunction(name, (leftName, leftProof), (rightName, rightProof)))
        }
    }
  }
}

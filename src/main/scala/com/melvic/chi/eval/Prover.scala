package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{
  Abstraction,
  Application,
  PLeft,
  PRight,
  TUnit,
  Variable,
  Conjunction => PConjunction,
  Disjunction => PDisjunction
}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Env
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}

object Prover {
  //noinspection SpellCheckingInspection
  def proveProposition(proposition: Proposition)(implicit env: Env): Result[Proof] =
    proposition match {
      case PUnit                    => Result.success(TUnit)
      case atom: Atom               => proveAtom(atom)
      case conjunction: Conjunction => proveConjunction(conjunction)
      case Disjunction(left, right) =>
        proveProposition(left)
          .map(PLeft)
          .orElse(proveProposition(right).map(PRight))
      case Implication(antecedent, consequent) => proveImplication(antecedent, consequent)
    }

  def proveImplication(antecedent: Proposition, consequent: Proposition)(
      implicit env: Env
  ): Result[Proof] = {
    val (term, newEnv) = Env.register(antecedent)
    proveProposition(consequent)(newEnv).map(Abstraction(term, _))
  }

  def proveAtom(atom: Atom)(implicit env: Env): Result[Proof] =
    proveFromAtom(atom)
      .orElse(proveFromImplication(atom))
      .orElse(proveFromDisjunction(atom))

  def proveFromAtom(atom: Atom)(implicit env: Env): Result[Proof] =
    Env
      .find {
        case Variable(_, `atom`) => true
      }
      .toRight(Fault.cannotProve(atom))

  def proveFromImplication(atom: Atom)(implicit env: Env): Result[Proof] = {
    val implicationOpt = Env.filterByConsequent(atom).headOption
    implicationOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap {
        case variable @ Variable(_, Implication(antecedent, consequent)) =>
          val newEnv = Env.without(variable)

          def recurse(function: Proof, in: Proposition, out: Proposition): Result[Proof] =
            proveProposition(in)(newEnv)
              .map(implicationElimination(function, _))
              .flatMap { application =>
                out match {
                  case `atom`                     => Result.success(application)
                  case Implication(newIn, newOut) => recurse(application, newIn, newOut)
                }
              }

          recurse(variable, antecedent, consequent).orElse(proveAtom(atom)(newEnv))
      }
  }

  def proveFromDisjunction(atom: Atom)(implicit env: Env): Result[Proof] = {
    val disjunctionOpt = Env.find {
      case Variable(_, _: Disjunction) => true
    }
    disjunctionOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap {
        case variable @ Variable(name, disjunction: Disjunction) =>
          proveDisjunction(name, disjunction, atom)(Env.without(variable))
      }
  }

  /**
    * Finds proof for the components and use them to construct the proof for the
    * conjunction.
    */
  def proveConjunction(conjunction: Conjunction)(implicit env: Env): Result[Proof] = {
    def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
      components match {
        case Nil => Result.success(result)
        case component :: rest =>
          proveProposition(component).flatMap(proof => recurse(proof :: result, rest))
      }

    val evaluatedComponents = recurse(Nil, conjunction.components)
    evaluatedComponents.map(components => PConjunction(components.reverse))
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
  def implicationElimination(function: Proof, argument: Proof): Application =
    argument match {
      case TUnit                    => Application(function, Nil)
      case Proof.Conjunction(terms) => Application(function, terms)
      case param                    => Application(function, List(param))
    }

  def proveDisjunction(name: String, disjunction: Disjunction, consequent: Proposition)(
      implicit env: Env
  ): Result[Proof] = {

    /**
      * Proves the consequent by assuming that the given component is in the environment.
      */
    def proveWithComponent(component: Proposition): Result[(Proof, Proof)] = {
      val (proofId, newEnv) = Env.register(component)
      proveProposition(consequent)(newEnv).map((proofId, _))
    }

    val Disjunction(left, right) = disjunction

    // Check if we can prove the consequent by with both components
    proveWithComponent(left).flatMap {
      case (Variable(leftName, _), leftProof) =>
        proveWithComponent(right).flatMap {
          case (Variable(rightName, _), rightProof) =>
            Result.success(PDisjunction(name, (leftName, leftProof), (rightName, rightProof)))
        }
    }
  }
}

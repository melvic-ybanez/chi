package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Env
import com.melvic.chi.output.Result.Result
import com.melvic.chi.output.{Fault, Result}

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
      .orElse(proveFromProduct(atom))
      .orElse(proveFromFunction(atom))
      .orElse(proveFromProductConsequent(atom))
      .orElse(proveFromEither(atom))

  def proveFromAtom(atom: Atom)(implicit env: Env): Result[Proof] =
    Env
      .find {
        case Variable(_, `atom`) => true
      }
      .toRight(Fault.cannotProve(atom))

  def proveFromFunction(atom: Atom)(implicit env: Env): Result[Proof] = {
    val implicationOpt = Env.filterByConsequent(atom).headOption
    implicationOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap {
        case variable @ Variable(_, Implication(antecedent, consequent)) =>
          val newEnv = Env.without(variable)
          applyRecurse(atom, variable, antecedent, consequent, newEnv) {
            case (`atom`, application) => Result.success(application)
          }.orElse(proveAtom(atom)(newEnv))
      }
  }

  /**
    * Recursively apply the function to get to the rightmost proposition.
    * This is in case the function is curried and requires multiple applications.
    */
  private def applyRecurse(atom: Atom, function: Proof, in: Proposition, out: Proposition, env: Env)(
      f: (Proposition, Application) => Result[Proof]
  ): Result[Proof] =
    proveProposition(in)(env)
      .map(implicationElimination(function, _))
      .flatMap { application =>
        out match {
          case Implication(newIn, newOut) => applyRecurse(atom, application, newIn, newOut, env)(f)
          case _                          => f(out, application)
        }
      }

  def proveFromProduct(atom: Atom)(implicit env: Env): Result[Proof] =
    findAndThen(atom, { case Variable(_, _: Conjunction) => true }) {
      case Variable(name, conjunction: Conjunction) =>
        proveProposition(Implication(conjunction, atom)).map(Match(name, _))
    }

  def proveFromProductConsequent(atom: Atom)(implicit env: Env): Result[Proof] = {
    val predicate: PartialFunction[Proof, Boolean] = {
      case Variable(_, function: Implication) =>
        Proposition.rightMostOf(function) match {
          case Conjunction(components) =>
            def find(components: List[Proposition]): Boolean =
              components.exists {
                case Conjunction(cs) => find(cs)
                case `atom`          => true
                case _               => false
              }

            find(components)
          case _ => false
        }
    }

    findAndThen(atom, predicate) {
      case variable @ Variable(_, Implication(in, out)) =>
        val newEnv = Env.without(variable)
        applyRecurse(atom, variable, in, out, newEnv) {
          case (Conjunction(components), application) =>
            proveFromProductComponents(atom, application, components)
        }.orElse(proveAtom(atom)(newEnv))
    }
  }

  def proveFromProductComponents(
      atom: Atom,
      application: Proof,
      components: List[Proposition]
  ): Result[Proof] = {
    def recurse(proof: Proof, components: List[Proposition], index: Int): Result[Proof] = {
      lazy val attr = Indexed(proof, index)

      components match {
        case Nil                     => Result.success(attr)
        case `atom` :: _             => Result.success(attr)
        case Conjunction(cs) :: rest => recurse(attr, cs, 1).orElse(recurse(proof, rest, index + 1))
        case _ :: rest               => recurse(proof, rest, index + 1)
      }
    }

    recurse(application, components, 1)
  }

  def proveFromEither(atom: Atom)(implicit env: Env): Result[Proof] =
    findAndThen(atom, { case Variable(_, _: Disjunction) => true }) {
      case variable @ Variable(name, disjunction: Disjunction) =>
        proveWithEither(name, disjunction, atom)(Env.without(variable))
    }

  def findAndThen(atom: Atom, predicate: PartialFunction[Proof, Boolean])(
      f: Proof => Result[Proof]
  )(implicit env: Env): Result[Proof] =
    Env.find(predicate).toRight(Fault.cannotProve(atom)).flatMap(f)

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

  def proveWithEither(name: String, disjunction: Disjunction, consequent: Proposition)(
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

    // Check if we can prove the consequent with both components
    proveWithComponent(left).flatMap {
      case (leftIn, leftOut) =>
        proveWithComponent(right).flatMap {
          case (rightIn, rightOut) =>
            val left = Abstraction(leftIn, leftOut)
            val right = Abstraction(rightIn, rightOut)
            Result.success(Match(name, EitherCases(left, right)))
        }
    }
  }
}

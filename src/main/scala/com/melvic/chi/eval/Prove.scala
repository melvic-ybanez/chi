package com.melvic.chi.eval

import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Env
import com.melvic.chi.output.Result.Result
import com.melvic.chi.output.{Fault, Result}

object Prove {
  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Env): Result[Proof] =
    proposition match {
      case PUnit                    => Result.success(TUnit)
      case atom: Atom               => Prove.atom(atom)
      case conjunction: Conjunction => Prove.fromConjunction(conjunction)
      case Disjunction(left, right) =>
        Prove
          .proposition(left)
          .map(PLeft)
          .orElse(Prove.proposition(right).map(PRight))
      case Implication(antecedent, consequent) => implication(antecedent, consequent)
      case Labeled(_, proposition)             => Prove.proposition(proposition)
    }

  def implication(antecedent: Proposition, consequent: Proposition)(implicit env: Env): Result[Proof] = {
    val (term, newEnv) = Env.register(antecedent)
    Prove.proposition(consequent)(newEnv).map(Abstraction(term, _))
  }

  def atom(atom: Atom)(implicit env: Env): Result[Proof] =
    findAndThen(atom, { case Variable(_, `atom`) => true })(Result.success)
      .orElse(atomFromProduct(atom))
      .orElse(atomFromFunction(atom))
      .orElse(atomFromProductConsequent(atom))
      .orElse(atomFromEither(atom))

  def atomFromFunction(atom: Atom)(implicit env: Env): Result[Proof] = {
    val implicationOpt = Env.filterByConsequent(atom).headOption
    implicationOpt
      .toRight(Fault.cannotProve(atom))
      .flatMap { case variable @ Variable(_, Implication(antecedent, consequent)) =>
        val newEnv = Env.without(variable)
        applyRecurse(atom, variable, antecedent, consequent, newEnv) { case (`atom`, application) =>
          Result.success(application)
        }.orElse(Prove.atom(atom)(newEnv))
      }
  }

  /**
   * Recursively apply the function to get to the rightmost proposition. This is in case the function is
   * curried and requires multiple applications.
   */
  private def applyRecurse(atom: Atom, function: Proof, in: Proposition, out: Proposition, env: Env)(
      f: (Proposition, Application) => Result[Proof]
  ): Result[Proof] =
    Prove
      .proposition(in)(env)
      .map(implicationElimination(function, _))
      .flatMap { application =>
        out match {
          case Implication(newIn, newOut) => applyRecurse(atom, application, newIn, newOut, env)(f)
          case _                          => f(out, application)
        }
      }

  def atomFromProduct(atom: Atom)(implicit env: Env): Result[Proof] =
    findAndThen(atom, { case Variable(_, _: Conjunction) => true }) {
      case variable @ Variable(name, conjunction: Conjunction) =>
        Prove.proposition(Implication(conjunction, atom))(Env.without(variable)).map(Match(name, _))
    }

  def atomFromProductConsequent(atom: Atom)(implicit env: Env): Result[Proof] = {
    val predicate: PartialFunction[Proof, Boolean] = { case Variable(_, function: Implication) =>
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

    findAndThen(atom, predicate) { case variable @ Variable(_, Implication(in, out)) =>
      val newEnv = Env.without(variable)
      applyRecurse(atom, variable, in, out, newEnv) { case (Conjunction(components), application) =>
        atomFromProductComponents(atom, application, components)
      }.orElse(Prove.atom(atom)(newEnv))
    }
  }

  def atomFromProductComponents(
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

  def atomFromEither(atom: Atom)(implicit env: Env): Result[Proof] =
    findAndThen(atom, { case Variable(_, _: Disjunction) => true }) {
      case variable @ Variable(name, disjunction: Disjunction) =>
        Prove.withEither(name, disjunction, atom)(Env.without(variable))
    }

  def findAndThen(atom: Atom, predicate: PartialFunction[Proof, Boolean])(
      f: Proof => Result[Proof]
  )(implicit env: Env): Result[Proof] =
    Env.find(predicate).toRight(Fault.cannotProve(atom)).flatMap(f)

  /**
   * Finds proof for the components and use them to construct the proof for the conjunction.
   */
  def fromConjunction(conjunction: Conjunction)(implicit env: Env): Result[Proof] = {
    def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
      components match {
        case Nil => Result.success(result)
        case component :: rest =>
          proposition(component).flatMap(proof => recurse(proof :: result, rest))
      }

    val evaluatedComponents = recurse(Nil, conjunction.components)
    evaluatedComponents.map(components => PConjunction(components.reverse))
  }

  /**
   * Provides proof for the consequent of the implication. This is based on the following rule for
   * implication-elimination: A A => B
   * ---------- (=>-E) B In programming, it means applying the function to the argument.
   */
  def implicationElimination(function: Proof, argument: Proof): Application =
    argument match {
      case TUnit                    => Application(function, Nil)
      case Proof.Conjunction(terms) => Application(function, terms)
      case param                    => Application(function, List(param))
    }

  def withEither(name: String, disjunction: Disjunction, consequent: Proposition)(implicit
      env: Env
  ): Result[Proof] = {

    /**
     * Proves the consequent by assuming that the given component is in the environment.
     */
    def proveWithComponent(component: Proposition): Result[(Proof, Proof)] = {
      val (proofId, newEnv) = Env.register(component)
      proposition(consequent)(newEnv).map((proofId, _))
    }

    val Disjunction(left, right) = disjunction

    // Check if we can prove the consequent with both components
    proveWithComponent(left).flatMap { case (leftIn, leftOut) =>
      proveWithComponent(right).flatMap { case (rightIn, rightOut) =>
        val left = Abstraction(leftIn, leftOut)
        val right = Abstraction(rightIn, rightOut)
        Result.success(Match(name, EitherCases(left, right)))
      }
    }
  }
}

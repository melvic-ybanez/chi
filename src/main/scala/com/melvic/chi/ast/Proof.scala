package com.melvic.chi.ast

import com.melvic.chi.ast.Proposition.PUnit

sealed trait Proof

//noinspection SpellCheckingInspection
object Proof {
  sealed trait TUnit extends Proof
  case object TUnit extends TUnit
  final case class Variable(name: String, proposition: Proposition) extends Proof
  final case class Conjunction(components: List[Proof]) extends Proof
  final case class PRight(proof: Proof) extends Proof
  final case class PLeft(proof: Proof) extends Proof

  /**
   * A pair of abstractions. This is used when there are two ways to construct a proof (e.g. Scala's Either
   * pattern match)
   */
  final case class EitherCases(left: Abstraction, right: Abstraction) extends Proof

  final case class Match(name: Variable, proof: Proof) extends Proof

  final case class Abstraction(domain: Proof, codomain: Proof) extends Proof

  /**
   * Function application. The first parameter is a proof by itself, rather than a function name as a string,
   * in order to support invokations of curried functions (e.g `f(a)(b)`)
   */
  final case class Application(function: Proof, params: List[Proof]) extends Proof

  final case class Infix(left: Proof, right: Proof) extends Proof

  final case class Indexed(function: Proof, index: Int) extends Proof

  def rename(proof: Proof, vars: List[Variable], newVars: List[Variable]): Proof =
    vars.zip(newVars).foldLeft(proof) { case (acc, (Variable(name, _), newVar)) =>
      map(acc) {
        case Variable(`name`, _) => newVar
        case variable            => variable
      }
    }

  def map(proof: Proof)(f: Variable => Proof): Proof =
    proof match {
      case v: Variable                   => f(v)
      case Conjunction(components)       => Conjunction(components.map(map(_)(f)))
      case PLeft(proof)                  => PLeft(map(proof)(f))
      case PRight(proof)                 => PRight(map(proof)(f))
      case Abstraction(in, out)          => Abstraction(map(in)(f), map(out)(f))
      case Application(function, params) => Application(map(function)(f), params.map(map(_)(f)))
      case Match(name, proof)            => Match(map(name)(f) match { case v: Variable => v }, map(proof)(f))
      case EitherCases(left, right) =>
        EitherCases(
          map(left)(f) match {
            case abstraction: Abstraction => abstraction
          },
          map(right)(f) match {
            case abstraction: Abstraction => abstraction
          }
        )
    }

  def fold[A](proof: Proof, init: A)(f: (A, Variable) => A): A =
    proof match {
      case unit: TUnit => init
      case v: Variable => f(init, v)
      case Conjunction(components) =>
        components.foldLeft(init) { (acc, proof) =>
          fold(proof, acc)(f)
        }
      case PLeft(proof)         => fold(proof, init)(f)
      case PRight(proof)        => fold(proof, init)(f)
      case Abstraction(in, out) => fold(out, fold(in, init)(f))(f)
      case Application(function, params) =>
        params.foldLeft(fold(function, init)(f)) { (acc, proof) =>
          fold(proof, acc)(f)
        }
    }

  object Variable {
    def fromName(name: String): Variable =
      Variable(name, PUnit)
  }

  object Application {
    def ofUnary(function: Proof, arg: Proof): Application =
      Application(function, arg :: Nil)
  }
}

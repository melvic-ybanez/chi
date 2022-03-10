package com.melvic.chi.ast

sealed trait Proposition

object Proposition {
  sealed trait Atom extends Proposition {
    def value: String
  }

  object Atom {
    def unapply(atom: Atom): Option[String] =
      Some(atom.value)
  }

  sealed trait PUnit extends Atom {
    override def value = "()"
  }

  case object PUnit extends PUnit
  final case class Identifier(value: String) extends Atom

  /**
    * Logical conjunction. We are using a list for the components, instead of modeling
    * them as a pair or as a Cons list (with a head and a tail), because we want to
    * easily distinguished a flat tuple from a nested one.
    */
  final case class Conjunction(components: List[Proposition]) extends Proposition

  final case class Disjunction(left: Proposition, right: Proposition) extends Proposition
  final case class Implication(antecedent: Proposition, consequent: Proposition) extends Proposition

  def fold[A](proposition: Proposition, init: A)(f: (A, Atom) => A): A =
    proposition match {
      case atom: Atom => f(init, atom)
      case Conjunction(components) =>
        components.foldLeft(init) { (acc, proposition) =>
          fold(proposition, acc)(f)
        }
      case Disjunction(left, right) =>
        fold(right, fold(left, init)(f))(f)
      case Implication(antecedent, consequent) =>
        fold(consequent, fold(antecedent, init)(f))(f)
    }

  def filter(proposition: Proposition)(f: Atom => Boolean): List[Atom] =
    fold(proposition, List.empty[Atom]) { (acc, atom) =>
      if (f(atom)) atom :: acc
      else acc
    }

  def exists(proposition: Proposition)(f: Atom => Boolean): Boolean =
    filter(proposition)(f).nonEmpty

  def show(proposition: Proposition): String =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + components.map(show).mkString(", ") + ")"
      case Disjunction(left, right)            => s"Either[${show(left)}, ${show(right)}]"
      case Implication(antecedent, consequent) => s"(${show(antecedent)} => ${show(consequent)})"
    }
}

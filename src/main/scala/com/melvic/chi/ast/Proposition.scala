package com.melvic.chi.ast

import scala.annotation.tailrec

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
    * easily distinguished a flat tuple from a nested one (which is very important during
    * stringification.)
    */
  final case class Conjunction(components: List[Proposition]) extends Proposition

  /**
    * Logical disjunction. Note that we may also end up supporting a list of components, just
    * like with conjunction, in the future (when adding support for languages that allow primitive
    * union types)
    */
  final case class Disjunction(left: Proposition, right: Proposition) extends Proposition

  final case class Implication(antecedent: Proposition, consequent: Proposition) extends Proposition

  /**
    * Like [[Disjunction]], but with all the components flattened. This is the dual of [[Conjunction]].
    */
  final case class Union(components: List[Proposition]) extends Proposition

  /**
    * Combines the parameter types, if any, with the return type to
    * form the full proposition of the signature.
    * For example:
    * `def foo[A, B](a: A, b: B): A` should return the proposition `(A, B) => A`
    */
  def fromSignature(signature: Signature): Proposition = {
    val out = signature.returnType
    signature.params match {
      case Nil          => out
      case param :: Nil => Implication(param.proposition, out)
      case params       => Implication(Conjunction(params.map(_.proposition)), out)
    }
  }

  /**
    * Renames the atoms in the proposition. This is needed to check isomorphism
    * between two propositions with different names for the variables.
    */
  def rename(proposition: Proposition, atoms: List[Atom], newAtoms: List[Atom]): Proposition =
    atoms.zip(newAtoms).foldLeft(proposition) {
      case (acc, (atom, newAtom)) =>
        map(acc)(a => if (a == atom) newAtom else a)
    }

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
      case Union(components) =>
        components.foldLeft(init) { (acc, proposition) =>
          fold(proposition, acc)(f)
        }
    }

  def map(proposition: Proposition)(f: Atom => Proposition): Proposition =
    proposition match {
      case atom: Atom               => f(atom)
      case Conjunction(components)  => Conjunction(components.map(map(_)(f)))
      case Disjunction(left, right) => Disjunction(map(left)(f), map(right)(f))
      case Implication(in, out)     => Implication(map(in)(f), map(out)(f))
      case Union(components)        => Union(components.map(map(_)(f)))
    }

  def filter(proposition: Proposition)(f: Atom => Boolean): List[Atom] =
    fold(proposition, List.empty[Atom]) { (acc, atom) =>
      if (f(atom)) atom :: acc
      else acc
    }

  def exists(proposition: Proposition)(f: Atom => Boolean): Boolean =
    filter(proposition)(f).nonEmpty

  def atoms(proposition: Proposition): List[Atom] =
    filter(proposition)(_ => true).distinct

  @tailrec
  def rightMostOf(implication: Implication): Proposition =
    implication match {
      case Implication(_, consequent: Implication) => rightMostOf(consequent)
      case Implication(_, consequent)              => consequent
    }

  /**
    * Combine antecedents in a nested implication such that
    * A => B => C becomes (A, B) => C. This is useful for computing isomorphisms.
    */
  def normalize(proposition: Proposition): Proposition =
    proposition match {
      case Implication(in, Implication(in1, out)) =>
        // combine the exponents and recurse
        normalize(Implication(Conjunction(in :: in1 :: Nil), out))
      case Implication(in, conjunction: Conjunction) =>
        normalize(conjunction) match {
          case Conjunction(cs) =>
            // distribute the exponent over the components
            val components = cs.map {
              // if the component already has a product exponent, multiply it with the outer one
              case Implication(Conjunction(cs1), out) => Implication(Conjunction(in :: cs1), out)
              case Implication(in1, out)              => Implication(Conjunction(in :: in1 :: Nil), out)

              // implication has no exponents, create one
              case p => Implication(in, p)
            }
            Conjunction(components)
        }
      case Conjunction(components) =>
        // flatten a conjunction
        val (found, newComponents) = components.foldLeft(false, List.empty[Proposition]) {
          case ((_, acc), Conjunction(cs)) => (true, acc ++ cs)
          case ((found, acc), c)           => (found, c :: acc)
        }
        val flat = Conjunction(newComponents)

        // see if there are changes in the components. If there are, try normalizing again
        if (found) normalize(flat) else flat
      case Implication(Disjunction(left, right), out) =>
        normalize(Conjunction(normalize(Implication(left, out)) :: normalize(Implication(right, out)) :: Nil))
      case Implication(in, disjunction: Disjunction) => normalize(Implication(in, normalize(disjunction)))
      case disjunction: Disjunction => Union.fromDisjunction(disjunction)
      case _                        => proposition
    }

  def isomorphic(proposition: Proposition, proposition1: Proposition): Boolean = {
    def similarComponents(cs: List[Proposition], cs1: List[Proposition]): Boolean =
      cs.length == cs1.length && cs.forall(c => cs1.exists(isomorphic(c, _))) &&
        cs1.forall(c => cs.exists(isomorphic(c, _)))

    (proposition, proposition1) match {
      case (Conjunction(cs), Conjunction(cs1))            => similarComponents(cs, cs1)
      case (Union(cs), Union(cs1))                        => similarComponents(cs, cs1)
      case (Disjunction(l, r), Disjunction(l1, r1))       => isomorphic(l, r) && isomorphic(l1, r1)
      case (Implication(in, out), Implication(in1, out1)) => isomorphic(out, out1) && isomorphic(in, in1)
      case (p: Proposition, p1: Proposition)              => p == p1
    }
  }

  object Union {

    /**
      * Creates a union by recursively visiting the components of a disjunction
      * and storing each of them into the component list of the resulting union
      */
    def fromDisjunction(disjunction: Disjunction): Union = {
      def recurse(disjunction: Disjunction, components: List[Proposition]): List[Proposition] =
        disjunction match {
          case Disjunction(left: Disjunction, right: Disjunction) =>
            recurse(right, recurse(left, components))
          case Disjunction(left: Disjunction, right) => recurse(left, right :: components)
          case Disjunction(left, right: Disjunction) => recurse(right, left :: components)
          case Disjunction(left, right)              => left :: right :: components
        }

      Union(recurse(disjunction, Nil))
    }
  }
}

package com.melvic.chi.env

import com.melvic.chi.ast.Proof.{TUnit, Variable}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}

/**
  * The set of assumptions and discharged formulae.
  */
final case class Env(proofs: List[Proof])

object Env {
  def default: Env = Env(List(TUnit))

  def fromList(proofs: List[Proof]): Env =
    Env(proofs)

  def fromListWithDefault(proofs: List[Proof]): Env =
    fromList(proofs ++ Env.default.proofs)

  def find(predicate: PartialFunction[Proof, Boolean])(implicit env: Env): Option[Proof] =
    filter(predicate).headOption

  def filter(predicate: PartialFunction[Proof, Boolean])(implicit env: Env): List[Proof] =
    env.proofs.filter(predicate.orElse(_ => false))

  def filterByConsequent(consequent: Proposition)(implicit env: Env): List[Proof] =
    filter {
      case Variable(_, function: Implication) if Proposition.rightMostOf(function) == consequent => true
    }

  def without(proof: Proof)(implicit env: Env): Env =
    fromList(env.proofs.filterNot(_ == proof))

  def addProof(proof: Proof)(implicit env: Env): Env =
    fromList(proof :: env.proofs)

  /**
    * Assigns a variable to the proposition and registers it into the environment
    */
  def register(proposition: Proposition)(implicit env: Env): (Proof, Env) =
    proposition match {
      case Atom(value) => registerSingle(value.toLowerCase.head.toString, proposition)
      case Conjunction(components) =>
        val (ids, newEnv) = components.foldLeft(List.empty[Proof], env) {
          case ((ids, env), component) =>
            val (id, newEnv) = register(component)(env)
            (id :: ids, newEnv)
        }
        (Proof.Conjunction(ids.reverse), newEnv)
      case Disjunction(_, _) => registerSingle("e", proposition)
      case Implication(_, _) => registerSingle("f", proposition)
    }

  def registerSingle(base: String, proposition: Proposition)(
      implicit env: Env
  ): (Proof, Env) = {
    val variable = Variable(generateName(base), proposition)
    (variable, fromList(variable :: env.proofs))
  }

  /**
    * Generates a variable name
    * @param base the base or root name of the variable
    * @param count used as a suffix to distinguish variables with the same base
    */
  def generateName(base: String, count: Int = 0)(implicit env: Env): String = {
    val name = base + (if (count == 0) "" else count.toString)
    val nameOpt = env.proofs.find {
      case Variable(`name`, _) => true
      case _                   => false
    }
    nameOpt
      .map {
        case Variable(name, _) =>
          if (name.startsWith("z")) generateName(base, count + 1)
          else generateName((base.head + 1).toChar.toString, count)
      }
      .getOrElse(name)
  }
}

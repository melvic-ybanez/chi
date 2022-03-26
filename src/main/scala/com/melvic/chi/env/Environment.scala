package com.melvic.chi.env

import com.melvic.chi.ast.Proof.{TUnit, Variable}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}

object Environment {

  /**
    * The set of assumptions and discharged formulae.
    */
  type Environment = List[Proof]

  def default: Environment = List(TUnit)

  def fromList(list: List[Proof]): Environment = list ++ default

  def findAtom(atom: Atom)(implicit env: Environment): Option[Proof] =
    env.find {
      case Variable(_, `atom`)                 => true
      case Variable(_, Implication(_, `atom`)) => true
      case _                                   => false
    }

  def discharge(env: Environment, proof: Proof): Environment =
    env.filterNot(_ == proof)

  /**
    * Assigns a variable to the proposition and registers it into the environment
    */
  def register(proposition: Proposition)(implicit env: Environment): (Proof, Environment) =
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
      implicit env: Environment
  ): (Proof, Environment) = {
    val variable = Variable(generateName(base), proposition)
    (variable, variable :: env)
  }

  /**
    * Generates a variable name
    * @param base the base or root name of the variable
    * @param count used as a suffix to distinguish variables with the same base
    */
  private def generateName(base: String, count: Int = 0)(implicit env: Environment): String = {
    val name = base + (if (count == 0) "" else count.toString)
    val nameOpt = env.find {
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

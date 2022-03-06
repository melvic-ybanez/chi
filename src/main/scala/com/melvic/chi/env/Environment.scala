package com.melvic.chi.env

import com.melvic.chi.Proposition
import com.melvic.chi.Proposition.{Atom, Conjunction, Disjunction, Implication}
import com.melvic.chi.env.Identifier.{Group, Single}

object Environment {
  type Environment = List[Variable]

  final case class Variable(name: String, proposition: Proposition)

  def empty: Environment = Nil

  def findAtom(atom: Atom)(implicit env: Environment): Option[Variable] =
    env.find {
      case Variable(_, `atom`) => true
      case Variable(_, Implication(_, `atom`)) => true
    }

  def register(proposition: Proposition)(implicit env: Environment): (Identifier, Environment) =
    proposition match {
      case Atom(value) => registerSingle(value.toLowerCase.head.toString, proposition)
      case Conjunction(left, right) =>
        val (leftVar, lEnv) = register(left)
        val (rightVar, rEnv) = register(right)(lEnv)
        val variable = Group(List(leftVar, rightVar))
        (variable, rEnv)
      case Disjunction(left, right) => registerSingle("e", proposition)
      case Implication(antecedent, consequent) => registerSingle("f", proposition)
    }

  def registerSingle(base: String, proposition: Proposition)(implicit env: Environment): (Identifier, Environment) = {
    val identifier = Single(generateName(base))
    (identifier, Variable(identifier.name, proposition) :: env)
  }

  private def generateName(base: String, count: Int = 0)(implicit env: Environment): String = {
    val name = base + (if (count == 0) "" else count.toString)
    val nameOpt = env.find {
      case Variable(`name`, _) => true
      case _ => false
    }
    nameOpt
      .map { case Variable(name, _) =>
        if (name.startsWith("z")) generateName(base, count + 1)
        else generateName((base.head + 1).toChar.toString, count)
      }
      .getOrElse(name)
  }
}

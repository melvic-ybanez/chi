package com.melvic.chi

import com.melvic.chi.Proposition._
import com.melvic.chi.env.Environment.{Environment, Variable}
import com.melvic.chi.env.Identifier.Single
import com.melvic.chi.env.{Environment, Identifier}

object Evaluate {
  type Result = Either[Error, String]

  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Environment): Result =
    proposition match {
      case atom: Atom =>
        Environment
          .findAtom(atom)
          .toRight(Error(atom))
          .flatMap {
            case Variable(name, `atom`) => Right(name)
            case Variable(f, Implication(atom: Atom, _)) =>
              Evaluate.proposition(atom).map(param => s"$f($param)")
            case Variable(name, Implication(f: Conjunction, _)) =>
              Evaluate.proposition(f).map(param => s"$f($param)")
          }
      case Conjunction(components) =>
        def recurse(result: List[String], components: List[Proposition]): Either[Error, List[String]] =
          components match {
            case Nil => Right(result)
            case component :: rest =>
              Evaluate.proposition(component).flatMap(str => recurse(str :: result, rest))
          }

        val evaluatedComponents = recurse(Nil, components)

        evaluatedComponents.map(strs => "(" + strs.mkString(", ") + ")")
      case Disjunction(left, right) =>
        Evaluate
          .proposition(left)
          .map(l => s"Left($l)")
          .orElse(Evaluate.proposition(right))
          .map(r => s"Right($r)")
      case Implication(antecedent, consequent) =>
        val (identifier, newEnv) = Environment.register(antecedent)
        Evaluate.proposition(consequent)(newEnv).map { codomain =>
          identifier match {
            case single: Single => s"${Identifier.show(identifier)} => $codomain"
            case _              => s"{ case ${Identifier.show(identifier)} => $codomain\n  }"
          }
        }
    }

  def functionCode(functionCode: FunctionCode): Result = {
    val FunctionCode(name, typeParams, proposition) = functionCode
    Evaluate
      .proposition(proposition)(Environment.empty)
      .map { body =>
        val propositionString = Proposition.show(proposition)
        val signature = s"def ${name}[${typeParams.mkString(", ")}]: $propositionString"
        s"$signature =\n  $body"
      }
  }
}

package com.melvic.chi

import com.melvic.chi.Proposition._
import com.melvic.chi.env.Environment.{Environment, Variable}
import com.melvic.chi.env.Identifier.Single
import com.melvic.chi.env.{Environment, Identifier}

object Evaluate {
  //noinspection SpellCheckingInspection
  def apply(proposition: Proposition)(implicit env: Environment): Either[Error, String] =
    proposition match {
      case atom: Atom =>
        Environment
          .findAtom(atom)
          .toRight(Error(atom))
          .flatMap {
            case Variable(name, `atom`) => Right(name)
            case Variable(f, Implication(atom: Atom, _)) =>
              Evaluate(atom).map(param => s"$f($param)")
            case Variable(name, Implication(f: Conjunction, _)) =>
              Evaluate(f).map(param => s"$f($param)")
          }
      case Conjunction(left, right) =>
        for {
          l <- Evaluate(left)
          r <- Evaluate(right)
        } yield s"($l, $r)"
      case Disjunction(left, right) =>
        Evaluate(left)
          .map(l => s"Left($l)")
          .orElse(Evaluate(right))
          .map(r => s"Right($r)")
      case Implication(antecedent, consequent) =>
        val (identifier, newEnv) = Environment.register(antecedent)
        Evaluate(consequent)(newEnv).map { codomain =>
          identifier match {
            case single: Single => s"${Identifier.show(identifier)} => $codomain"
            case _ => s"{ case ${Identifier.show(identifier)} => $codomain\n  }"
          }
        }
    }
}

package com.melvic.chi

import com.melvic.chi.ast.Proof.{Abstraction, Application, PLeft, PRight, TUnit, Variable}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.env.Environment
import com.melvic.chi.env.Environment.Environment

object Evaluate {
  //noinspection SpellCheckingInspection
  def proposition(proposition: Proposition)(implicit env: Environment): Result[Proof] =
    proposition match {
      case PUnit => Result.success(TUnit)
      case atom: Atom =>
        Environment
          .findAtom(atom)
          .toRight(Fault.cannotProve(atom))
          .flatMap {
            case variable @ Variable(name, `atom`) => Result.success(variable)
            case variable @ Variable(f, Implication(antecedent, _)) =>
              Evaluate.proposition(antecedent)(env.filterNot(_ == variable)).map {
                case Proof.Conjunction(terms) => Application(f, terms)
                case param => Application(f, List(param))
              }
          }
      case Conjunction(components) =>
        def recurse(result: List[Proof], components: List[Proposition]): Result[List[Proof]] =
          components match {
            case Nil => Result.success(result)
            case component :: rest =>
              Evaluate.proposition(component).flatMap(str => recurse(str :: result, rest))
          }

        val evaluatedComponents = recurse(Nil, components)
        evaluatedComponents.map(Proof.Conjunction)
      case Disjunction(left, right) =>
        Evaluate
          .proposition(left)
          .map(PLeft)
          .orElse(Evaluate.proposition(right))
          .map(PRight)
      case Implication(antecedent, consequent) =>
        val (term, newEnv) = Environment.register(antecedent)
        Evaluate.proposition(consequent)(newEnv).map(Abstraction(term, _))
    }

  def functionCode(functionCode: FunctionCode): Result[String] = {
    val FunctionCode(name, typeParams, proposition) = functionCode
    Evaluate
      .proposition(proposition)(Environment.default)
      .map { body =>
        val propositionString = Proposition.show(proposition)
        val signature         = s"def ${name}[${typeParams.mkString(", ")}]: $propositionString"
        s"$signature =\n  ${Proof.show(body)}"
      }
  }

  def functionString(functionCode: String): Result[String] =
    Parser.parseFunctionCode(functionCode).flatMap(Evaluate.functionCode)
}

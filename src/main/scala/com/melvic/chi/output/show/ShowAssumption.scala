package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition._

object ShowAssumption {
  def apply(variable: Variable): String =
    s"Assume ${variable.name}: ${showProposition(variable.proposition)}"

  def showProposition(proposition: Proposition): String =
    proposition match {
      case Atom(value)              => value
      case Conjunction(components)  => Show.toCSV(components.map(showComponent), " & ")
      case Disjunction(left, right) => showComponent(left) + " | " + showComponent(right)
      case Implication(antecedent, consequent) =>
        s"${showComponent(antecedent)} => ${showProposition(consequent)}"
    }

  def showComponent(component: Proposition): String =
    if (isGroup(component)) "(" + showProposition(component) + ")"
    else showProposition(component)

  def isGroup: Proposition => Boolean = {
    case _: Union       => true
    case _: Disjunction => true
    case _: Implication => true
    case _: Conjunction => true
    case _              => false
  }
}

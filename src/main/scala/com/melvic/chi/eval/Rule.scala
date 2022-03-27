package com.melvic.chi.eval

import com.melvic.chi.ast.Proof
import com.melvic.chi.ast.Proposition.Atom
import com.melvic.chi.env.Environment

class Rule private (implicit env: Environment) {
  def atom(atom: Atom): Option[Proof] =
    Environment.findAssumption(atom)
}

object Rule {
  def apply(implicit env: Environment): Rule =
    new Rule
}

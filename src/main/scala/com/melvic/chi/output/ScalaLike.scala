package com.melvic.chi.output

import com.melvic.chi.ast.Proof.Variable

trait ScalaLike { show: Show =>
  def paramList(params: List[Variable], split: Boolean): String = {
    val vars = params.map { case Variable(name, proposition) =>
      s"$name: ${show.proposition(proposition)}"
    }
    s"(${Show.splitParams(vars, split, indentWidth)})"
  }
}

package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.{Conjunction, PUnit}

trait TuplesInParens { _: LanguageParser =>
  val conjunction: Parser[Proposition] =
    "(" ~> repsep(proposition, ",") <~ ")" ^^ {
      case Nil         => PUnit
      case proposition => Conjunction(proposition)
    }
}

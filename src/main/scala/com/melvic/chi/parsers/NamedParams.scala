package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable

import scala.util.parsing.combinator.RegexParsers

trait NamedParams { _: LanguageParser =>
  val param: Parser[Variable] = (nameParser ~ (":" ~> proposition)) ^^ {
    case name ~ proposition =>
      Variable(name, proposition)
  }

  val paramList: Parser[List[Variable]] = "(" ~> (repsep(param, ",") <~ ")")
}

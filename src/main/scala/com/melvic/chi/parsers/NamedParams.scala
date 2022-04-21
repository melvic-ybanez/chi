package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable

import scala.util.parsing.combinator.RegexParsers

trait NamedParams { _: RegexParsers =>
  val param: Parser[Variable]

  val paramList: Parser[List[Variable]] = "(" ~> (repsep(param, ",") <~ ")")
}

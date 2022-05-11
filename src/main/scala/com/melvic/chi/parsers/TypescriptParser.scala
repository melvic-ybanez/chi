package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition.{Conjunction, Disjunction, Implication}
import com.melvic.chi.ast.{Proposition, Signature}

object TypescriptParser extends LanguageParser with NamedParams {
  override val language = Language.Typescript

  /**
   * Example:
   * {{{
   * function foo<T>(bar: T, baz: string): T
   * }}}
   */
  override val signature =
    "function" ~> nameParser ~ opt(
      "<" ~> rep1sep(nameParser, ",") <~ ">"
    ) ~ paramList ~ (":" ~> proposition) ^^ { case name ~ typeParams ~ paramsList ~ proposition =>
      Signature(name, typeParams.getOrElse(Nil), paramsList, proposition)
    }

  override lazy val proposition: PackratParser[Proposition] =
    implication | conjunction | disjunction | identifier

  val conjunction: Parser[Conjunction] = "[" ~> repsep(proposition, ",") <~ "]" ^^ { Conjunction }

  val disjunction: Parser[Disjunction] = proposition ~ rep1("|" ~> proposition) ^^ {
    case left ~ (right :: rest) => Disjunction.fromList(left, right, rest)
  }

  /**
   * Example:
   * {{{
   * (foo: string, bar: number) => string
   * }}}
   */
  val implication: Parser[Implication] = paramList ~ ("=>" ~> proposition) ^^ {
    case (param :: Nil) ~ returnType => Implication(param.proposition, returnType)
    case params ~ returnType =>
      val paramTypes = Conjunction(params.map(_.proposition))
      Implication(paramTypes, returnType)
  }
}

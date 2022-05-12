package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Disjunction, Identifier, Implication, Labeled}
import com.melvic.chi.ast.{Proposition, Signature}

import scala.util.matching.Regex

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

  lazy val complex: PackratParser[Proposition] = implication | conjunction | disjunction

  override lazy val proposition: PackratParser[Proposition] =
    complex | identifier

  val conjunction: Parser[Conjunction] = "[" ~> repsep(proposition, ",") <~ "]" ^^ { Conjunction }

  val disjunction: Parser[Disjunction] = {
    // for now, disallow union of non-built-in types
    val validComponents = regex(new Regex(Language.Typescript.builtInTypes.mkString("|"))) ^^ { Identifier }

    val proposition = complex | validComponents
    proposition ~ rep1("|" ~> proposition) ^^ {
      case left ~ (right :: rest) => Disjunction.fromList(left, right, rest)
    }
  }

  /**
   * The antecedent of a Typescript implication can only be a list of name-type pairs.
   *
   * Example:
   * {{{
   * (foo: string, bar: number) => string
   * }}}
   */
  val implication: Parser[Implication] = paramList ~ ("=>" ~> proposition) ^^ {
    case ((variable: Variable) :: Nil) ~ returnType =>
      Implication(Labeled.fromVariable(variable), returnType)
    case params ~ returnType =>
      val paramTypes = params.map(Labeled.fromVariable)
      Implication(Conjunction(paramTypes), returnType)
  }
}

package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}

object PythonParser extends LanguageParser with NamedParams {
  override val language = Language.Python

  val conjunction: Parser[Proposition] = {
    val emptyParens: Parser[List[Atom]] = "(" ~ ")" ^^ { _ =>
      Nil
    }
    "Tuple" ~> "[" ~> (emptyParens | rep1sep(identifier, ",")) <~ "]" ^^ (types => Conjunction(types))
  }

  val disjunction: Parser[Proposition] =
    "Union" ~> "[" ~> (identifier ~ rep1sep(identifier, ",")) <~ "]" ^^ {
      case left ~ (right :: rest) => Disjunction.fromList(left, right, rest)
    }

  lazy val implication: PackratParser[Proposition] =
    "Callable" ~> "[" ~> "[" ~> repsep(proposition, ",") ~ ("]" ~> "," ~> proposition <~ "]") ^^ {
      case Nil ~ returnType    => Implication(PUnit, returnType)
      case params ~ returnType => Implication(Conjunction(params), returnType)
    }

  override val proposition: PackratParser[Proposition] =
    implication | conjunction | disjunction | identifier

  override val signature: Parser[Signature] =
    "def" ~> nameParser ~ paramList ~ ("->" ~> proposition) ^^ {
      case name ~ params ~ returnType => Signature(name, Nil, params, returnType)
    }
}

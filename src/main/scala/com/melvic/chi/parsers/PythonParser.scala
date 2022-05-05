package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}

object PythonParser extends LanguageParser with NamedParams {
  override val language = Language.Python

  val conjunction: Parser[Proposition] = {
    val emptyParens: Parser[List[Atom]] = "(" ~ ")" ^^ { _ =>
      Nil
    }
    "Tuple" ~> "[" ~> (emptyParens | rep1sep(proposition, ",")) <~ "]" ^^ (types => Conjunction(types))
  }

  val disjunction: Parser[Proposition] =
    "Union" ~> "[" ~> ((proposition <~ ",") ~ rep1sep(proposition, ",")) <~ "]" ^^ {
      case left ~ (right :: rest) => Disjunction.fromList(left, right, rest)
    }

  lazy val implication: PackratParser[Proposition] =
    "Callable" ~> "[" ~> "[" ~> repsep(proposition, ",") ~ ("]" ~> "," ~> proposition <~ "]") ^^ {
      case params ~ returnType => Implication(Conjunction(params), returnType)
    }

  override lazy val proposition: PackratParser[Proposition] =
    implication | conjunction | disjunction | identifier

  val typeVar: Parser[String] = nameParser <~ "=" <~ "TypeVar" <~ "(" <~ "'" <~ nameParser <~ "'" <~ ")"

  val typeVars: Parser[List[String]] = rep(typeVar)

  override val signature: Parser[Signature] =
    (typeVars <~ "def") ~ nameParser ~ paramList ~ ("->" ~> proposition) ^^ {
      case typeVars ~ name ~ params ~ returnType => Signature(name, typeVars, params, returnType)
    }
}

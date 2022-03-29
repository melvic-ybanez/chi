package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Identifier, Implication}
import com.melvic.chi.ast.{Proposition, Signature}

object JavaParser extends BaseParser {
  val language = Language.Java

  val typeParams: Parser[List[Atom]] = "<" ~> rep1sep(identifier, ",") <~ ">"

  val param: Parser[Variable] = (proposition ~ nameParser) ^^ {
    case proposition ~ name => Variable(name, proposition)
  }

  lazy val function: PackratParser[Implication] =
    ("Function" ~ "<") ~> proposition ~ ("," ~> proposition <~ ">") ^^ {
      case in ~ out => Implication(in, out)
    }

  lazy val biFunction: PackratParser[Implication] =
    ("BiFunction" ~> ("<" ~> repNM(3, 3, proposition, ",") <~ ">")) ^^ {
      case a :: b :: c :: _ =>
        Implication(Conjunction(a :: b :: Nil), c)
    }

  lazy val proposition: PackratParser[Proposition] = function | biFunction | identifier

  val functionCode: Parser[Signature] =
    typeParams ~ proposition ~ nameParser ~ opt(paramList) ^^ {
      case typeParams ~ returnType ~ name ~ params =>
        Signature(name, typeParams.map(_.value), params.getOrElse(Nil), returnType)
    }
}

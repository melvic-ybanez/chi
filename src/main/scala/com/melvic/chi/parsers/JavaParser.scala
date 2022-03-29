package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition.Atom
import com.melvic.chi.ast.{Proposition, Signature}

object JavaParser extends BaseParser {
  val language = Language.Java

  val typeParams: Parser[List[Atom]] = "<" ~> rep1sep(identifier, ",") <~ ">"

  val param: Parser[Variable] = (proposition ~ nameParser) ^^ {
    case proposition ~ name => Variable(name, proposition)
  }

  lazy val proposition: PackratParser[Proposition] = identifier

  val functionCode: Parser[Signature] =
    typeParams ~ identifier ~ nameParser ~ opt(paramList) ^^ {
      case typeParams ~ returnType ~ name ~ params =>
        Signature(name, typeParams.map(_.value), params.getOrElse(Nil), returnType)
    }
}

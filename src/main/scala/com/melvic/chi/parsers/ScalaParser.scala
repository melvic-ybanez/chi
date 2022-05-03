package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Signature

object ScalaParser extends ScalaParser

trait ScalaParser extends LanguageParser with ScalaLikeParser with NamedParams with TuplesInParens {
  val language = Language.Scala

  val scalaParser: Parser[Signature] =
    "def" ~> nameParser ~ opt("[" ~> rep1sep(nameParser, ",") <~ "]") ~ opt(paramList) ~ (":" ~> proposition) ^^ {
      case name ~ typeParams ~ paramList ~ proposition =>
        val params = paramList.getOrElse(Nil)
        Signature(name, typeParams.getOrElse(Nil), params, proposition)
    }

  val signature: Parser[Signature] = scalaParser
}

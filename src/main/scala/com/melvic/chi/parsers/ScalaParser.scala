package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Signature

object ScalaParser extends ScalaParser

trait ScalaParser extends LanguageParser with ScalaLikeParser with NamedParams with TuplesInParens {
  val language = Language.Scala

  val param: Parser[Variable] = (nameParser ~ (":" ~> proposition)) ^^ {
    case name ~ proposition =>
      Variable(name, proposition)
  }

  val scalaParser: Parser[Signature] =
    "def" ~> nameParser ~ opt("[" ~> rep1sep(identifier, ",") <~ "]") ~ opt(paramList) ~ (":" ~> proposition) ^^ {
      case name ~ typeParams ~ paramList ~ proposition =>
        val params = paramList.getOrElse(Nil)
        Signature(name, typeParams.getOrElse(Nil).map(_.value), params, proposition)
    }

  val signature: Parser[Signature] = scalaParser
}

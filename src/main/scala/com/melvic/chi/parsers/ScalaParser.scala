package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}

object ScalaParser extends ScalaParser {
  val signature: Parser[Signature] = scalaParser
}

trait ScalaParser extends BaseParser with NamedParams with TuplesInParens {
  val language = Language.Scala

  val disjunction: Parser[Disjunction] =
    "Either" ~> "[" ~> proposition ~ ("," ~> proposition <~ "]") ^^ {
      case left ~ right => Disjunction(left, right)
    }

  lazy val implication: PackratParser[Implication] =
    proposition ~ ("=>" ~> proposition) ^^ {
      case antecedent ~ consequent => Implication(antecedent, consequent)
    }

  lazy val proposition: PackratParser[Proposition] =
    implication | ("(" ~> implication <~ ")") | conjunction | disjunction | identifier

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
}

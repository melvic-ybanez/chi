package com.melvic.chi.parsers

import com.melvic.chi.Result
import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.out.Fault

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object ScalaParser extends BaseParser {
  val language = Language.Scala

  val conjunction: Parser[Proposition] =
    "(" ~> repsep(proposition, ",") <~ ")" ^^ {
      case Nil         => PUnit
      case proposition => Conjunction(proposition)
    }

  val disjunction: Parser[Disjunction] =
    "Either[" ~> proposition ~ ("," ~> proposition <~ "]") ^^ {
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

  val functionCode: Parser[Signature] =
    "def" ~> nameParser ~ opt("[" ~> rep1sep(identifier, ",") <~ "]") ~ opt(paramList) ~ (":" ~> proposition) ^^ {
      case name ~ typeParams ~ paramList ~ proposition =>
        val params = paramList.getOrElse(Nil)
        Signature(name, typeParams.getOrElse(Nil).map(_.value), params, proposition)
    }
}

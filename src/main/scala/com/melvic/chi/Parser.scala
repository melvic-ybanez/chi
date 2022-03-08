package com.melvic.chi

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.env.Environment
import com.melvic.chi.env.Environment.Environment

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

object Parser extends RegexParsers with PackratParsers {
  val atom: Parser[Atom] = "[a-zA-Z]+".r ^^ { Atom }

  val conjunction: Parser[Proposition] =
    "(" ~> repsep(proposition, ",") <~ ")" ^^ {
      case Nil         => PUnit
      case proposition => Conjunction(proposition)
    }

  val disjunction: Parser[Disjunction] =
    "Either[" ~> proposition ~ ("," ~> proposition <~ "]") ^^ { case left ~ right => Disjunction(left, right) }

  lazy val implication: PackratParser[Implication] =
    proposition ~ ("=>" ~> proposition) ^^ { case antecedent ~ consequent => Implication(antecedent, consequent) }

  lazy val proposition: PackratParser[Proposition] =
    implication | ("(" ~> implication <~ ")") | conjunction | disjunction | atom

  val identifier: Parser[String] = "[a-zA-Z]+".r

  val param: Parser[Variable] = (identifier ~ (":" ~> proposition)) ^^ {
    case name ~ proposition =>
      Variable(name, proposition)
  }

  val paramList: Parser[List[Variable]] = "(" ~> (repsep(param, ",") <~ ")")

  val functionCode: Parser[Signature] =
    "def" ~> identifier ~ opt("[" ~> rep1sep(atom, ",") <~ "]") ~ opt(paramList) ~ (":" ~> proposition) ^^ {
      case name ~ typeParams ~ paramList ~ proposition =>
        val params = paramList.getOrElse(Nil)
        Signature(name, typeParams.getOrElse(Nil).map(_.value), params, proposition)
    }

  def parseSignature(code: String): Result[Signature] =
    parseAll(functionCode, new PackratReader(new CharSequenceReader(code))) match {
      case Success(functionCode, _) => Right(functionCode)
      case Failure(msg, _)          => Left(Fault.parseError(msg))
    }
}

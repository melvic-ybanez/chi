package com.melvic.chi

import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.Proposition

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

object Parser extends RegexParsers with PackratParsers {
  val atom: Parser[Atom] = "[a-zA-Z]+".r ^^ { Atom }

  val conjunction: Parser[Proposition] =
    "(" ~> repsep(proposition, ",") <~ ")" ^^ {
      case Nil => PUnit
      case proposition => Conjunction(proposition)
    }

  val disjunction: Parser[Disjunction] =
    "Either[" ~> proposition ~ ("," ~> proposition <~ "]") ^^ { case left ~ right => Disjunction(left, right) }

  val implication: PackratParser[Implication] =
    proposition ~ ("=>" ~> proposition) ^^ { case antecedent ~ consequent => Implication(antecedent, consequent) }

  lazy val proposition: PackratParser[Proposition] =
     implication | ("(" ~> implication <~ ")") | conjunction | disjunction | atom

  def functionCode: Parser[FunctionCode] =
    "def" ~> "[a-zA-Z]+".r ~ ("[" ~> rep1sep(atom, ",") <~ "]") ~ (":" ~> proposition) ^^ {
      case name ~ typeParams ~ proposition =>
        FunctionCode(name, typeParams.map(_.value), proposition)
    }

  def parseFunctionCode(code: String): Result[FunctionCode] =
    parseAll(functionCode, new PackratReader(new CharSequenceReader(code))) match {
      case Success(functionCode, _) => Right(functionCode)
      case Failure(msg, _) => Left(Fault.parseError(msg))
    }
}

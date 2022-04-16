package com.melvic.chi.parsers

import com.melvic.chi.Result
import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition.{Atom, Identifier}
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.out.Fault

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

trait BaseParser extends RegexParsers with PackratParsers {
  val language: Language

  val signature: Parser[Signature]

  val param: Parser[Variable]

  val identifier: Parser[Atom] = nameParser ^^ { Identifier }

  lazy val nameParser: Parser[String] = "[a-zA-Z]+".r

  val paramList: Parser[List[Variable]] = "(" ~> (repsep(param, ",") <~ ")")

  val proposition: PackratParser[Proposition]

  def parseSignature(code: String): ParseSignature =
    parseAll(signature, new PackratReader(new CharSequenceReader(code))) match {
      case Success(signature, _) => Right(signature, language)
      case Failure(msg, _)       => Left(Fault.parseError(msg))
    }
}

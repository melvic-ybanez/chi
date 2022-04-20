package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition.{Atom, Identifier}
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.output.Fault

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

trait BaseParser extends RegexParsers with PackratParsers {
  val language: Language

  val signature: Parser[Signature]

  val identifier: Parser[Atom] = nameParser ^^ { Identifier }

  val proposition: PackratParser[Proposition]

  lazy val nameParser: Parser[String] = "[a-zA-Z]+[0-9]*".r

  def parseSignature(code: String): ParseSignature =
    parseAll(signature, new PackratReader(new CharSequenceReader(code))) match {
      case Success(signature, _) => Right(signature, language)
      case Failure(msg, _)       => Left(Fault.parseError(msg))
    }
}

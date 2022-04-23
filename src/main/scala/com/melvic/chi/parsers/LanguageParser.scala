package com.melvic.chi.parsers

import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.output.Fault

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

trait LanguageParser extends RegexParsers with PackratParsers with WithIdentifier {
  val language: Language

  val signature: Parser[Signature]

  val proposition: PackratParser[Proposition]

  def parseSignature(code: String): ParseSignature =
    parseAll(signature, new PackratReader(new CharSequenceReader(code))) match {
      case Success(signature, _) => Right(signature, language)
      case Failure(msg, _)       => Left(Fault.parseError(msg))
    }
}

package com.melvic.chi.parsers

import com.melvic.chi.ast.{Isomorphism, Signature}
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}

import scala.util.parsing.combinator.RegexParsers

object IsomorphismParser extends RegexParsers {
  def signature: Parser[Signature] =
    ScalaParser.signature | JavaParser.signature

  def pair: Parser[Isomorphism] =
    signature ~ ("<=>" ~> signature) ^^ {
      case left ~ right =>
        Isomorphism(left, right)
    }

  def parseIso(functions: String): Result[Isomorphism] =
    parseAll(signature, functions) match {
      case Success(isomorphism, _) => Result.success(isomorphism)
      case Failure(msg, _)         => Result.fail(Fault.parseError(msg))
    }
}

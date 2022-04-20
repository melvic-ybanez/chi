package com.melvic.chi.parsers

import com.melvic.chi.ast.{AssertIso, Signature}
import com.melvic.chi.output.Result.Result
import com.melvic.chi.output.{Fault, Result}

import scala.reflect.internal.util.NoPosition.end
import scala.util.parsing.input.CharSequenceReader

object IsomorphismParser extends ScalaParser {
  val signature: Parser[Signature] = scalaParser

  val pair: Parser[AssertIso] =
    signature ~ ("<=>" ~> signature) ^^ {
      case left ~ right =>
        AssertIso(left, right)
    }

  def parseIso(signatures: String): Result[AssertIso] =
    parseAll(pair, signatures) match {
      case Success(isomorphism, _) => Result.success(isomorphism)
      case Failure(msg, _)         => Result.fail(Fault.parseError(msg))
    }
}

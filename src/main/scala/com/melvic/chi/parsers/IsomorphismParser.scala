package com.melvic.chi.parsers

import com.melvic.chi.ast.{Isomorphism, Signature}
import com.melvic.chi.out.Result.Result
import com.melvic.chi.out.{Fault, Result}

object IsomorphismParser extends ScalaParser {
  val signature: Parser[Signature] = scalaParser

  val pair: Parser[Isomorphism] =
    signature ~ ("<=>" ~> signature) ^^ {
      case left ~ right =>
        Isomorphism(left, right)
    }

  def parseIso(functions: String): Result[Isomorphism] =
    parseAll(pair, functions) match {
      case Success(isomorphism, _) => Result.success(isomorphism)
      case Failure(msg, _)         => Result.fail(Fault.parseError(msg))
    }
}

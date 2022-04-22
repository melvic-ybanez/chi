package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.{Conjunction, Union}
import com.melvic.chi.output.Fault
import com.melvic.chi.output.Result.Result

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object AssumptionParser extends RegexParsers with PackratParsers with ScalaLikeParser {
  val AssumeOperator = "assume"

  override val conjunction: Parser[Proposition] =
    proposition ~ rep1("&" ~> proposition) ^^ { case left ~ right =>
      Conjunction(left :: right)
    }

  override val disjunction: Parser[Proposition] =
    proposition ~ rep1("|" ~> proposition) ^^ { case left ~ right =>
      Union(left :: right)
    }

  def assumption: Parser[Variable] =
    AssumeOperator ~> nameParser ~ (":" ~> proposition) ^^ { case name ~ proposition =>
      Variable(name, proposition)
    }

  def parseAssumption(input: String): Result[Variable] =
    parseAll(assumption, new PackratReader(new CharSequenceReader(input))) match {
      case Success(assumption, _) => Right(assumption)
      case Failure(msg, _) => Left(Fault.parseError(msg))
    }
}

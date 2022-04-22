package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.{Conjunction, Disjunction}
import com.melvic.chi.output.Fault
import com.melvic.chi.output.Result.Result

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object AssumptionParser extends RegexParsers with PackratParsers with ScalaLikeParser {
  val AssumeOperator = "assume"

  override val conjunction: Parser[Proposition] = {
    val and = proposition ~ rep1("&" ~> proposition) ^^ {
      case left ~ right =>
        Conjunction(left :: right)
    }
    and | ("(" ~> and <~ ")")
  }

  /**
    * The equivalent of the Either type.
    * Note: Let's just keep the number of components at 2 for now. It would be more
    * compatible with Either type of both Scala and Haskell.
    */
  override val disjunction: Parser[Proposition] = {
    val or = proposition ~ ("|" ~> proposition) ^^ {
      case left ~ right => Disjunction(left, right)
    }
    or | ("(" ~> or <~ ")")
  }

  def assumption: Parser[Variable] =
    AssumeOperator ~> nameParser ~ (":" ~> proposition) ^^ {
      case name ~ proposition =>
        Variable(name, proposition)
    }

  def parseAssumption(input: String): Result[Variable] =
    parseAll(assumption, new PackratReader(new CharSequenceReader(input))) match {
      case Success(assumption, _) => Right(assumption)
      case Failure(msg, _)        => Left(Fault.parseError(msg))
    }
}

package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.ast.Proposition.{Conjunction, Disjunction, Implication, PUnit}

object HaskellParser extends BaseParser with TuplesInParens {
  override val language = Language.Haskell

  val disjunction: Parser[Disjunction] =
    "Either" ~> proposition ~ proposition ^^ {
      case left ~ right => Disjunction(left, right)
    }

  val base: Parser[Proposition] = {
    val base = conjunction | disjunction | identifier
    base | ("(" ~> base <~ ")")
  }

  lazy val proposition: PackratParser[Proposition] =
    base ~ opt("->" ~> base) ^^ {
      case in ~ Some(out) => Implication(in, out)
      case in ~ None => in
    }

  override val signature = ident ~ ("::" ~> proposition) ^^ {
    case functionName ~ functionType =>
      val typeParams = Proposition.atoms(functionType).map(_.value)
      Signature(functionName, typeParams, Nil, functionType)
  }
}

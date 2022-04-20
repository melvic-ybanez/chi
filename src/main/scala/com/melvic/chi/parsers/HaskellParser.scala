package com.melvic.chi.parsers

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.ast.Proposition.{Conjunction, Disjunction, Implication, PUnit}

object HaskellParser extends BaseParser with TuplesInParens {
  override val language = Language.Haskell

  lazy val disjunction: PackratParser[Disjunction] = {
    val component = base | implWithParens

    "Either" ~> (component ~ component) ^^ {
      case left ~ right => Disjunction(left, right)
    }
  }

  lazy val implWithParens: PackratParser[Implication] =
    "(" ~> implication <~ ")"

  lazy val base: PackratParser[Proposition] = disjunction | conjunction | identifier

  lazy val implication: PackratParser[Implication] =
    proposition ~ ("->" ~> proposition) ^^ {
      case antecedent ~ consequent => Implication(antecedent, consequent)
    }

  lazy val proposition: PackratParser[Proposition] =
    implication | implWithParens | base

  override val signature = nameParser ~ ("::" ~> proposition) ^^ {
    case functionName ~ functionType =>
      val typeParams = Proposition.atoms(functionType).map(_.value)
      Signature(functionName, typeParams, Nil, functionType)
  }
}

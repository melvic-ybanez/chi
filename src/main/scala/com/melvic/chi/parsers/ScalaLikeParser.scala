package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition
import com.melvic.chi.ast.Proposition.{Atom, Disjunction, Implication}

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

trait ScalaLikeParser extends WithIdentifier { _: RegexParsers with PackratParsers =>
  val identifier: Parser[Atom]

  val conjunction: Parser[Proposition]

  val disjunction: Parser[Proposition] =
    "Either" ~> "[" ~> proposition ~ ("," ~> proposition <~ "]") ^^ {
      case left ~ right => Disjunction(left, right)
    }

  lazy val implication: PackratParser[Implication] =
    proposition ~ ("=>" ~> proposition) ^^ {
      case antecedent ~ consequent => Implication(antecedent, consequent)
    }

  lazy val proposition: PackratParser[Proposition] =
    implication | ("(" ~> implication <~ ")") | conjunction | disjunction | identifier
}

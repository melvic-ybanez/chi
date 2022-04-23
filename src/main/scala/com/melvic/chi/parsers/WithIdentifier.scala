package com.melvic.chi.parsers

import com.melvic.chi.ast.Proposition.{Atom, Identifier}

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

trait WithIdentifier { _: RegexParsers with PackratParsers =>
  val identifier: Parser[Atom] = nameParser ^^ { Identifier }

  lazy val nameParser: Parser[String] = "[a-zA-Z]+[0-9]*".r

}

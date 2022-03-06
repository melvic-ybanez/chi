package com.melvic.chi.env

sealed trait Identifier

object Identifier {
  final case class Single(name: String) extends Identifier

  final case class Group(identifiers: List[Identifier]) extends Identifier

  def show(variable: Identifier): String =
    variable match {
      case Single(name) => name
      case Group(identifiers) =>
        "(" + identifiers.map(show).mkString(", ") + ")"
    }
}

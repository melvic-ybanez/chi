package com.melvic.chi.parsers

sealed trait Language {
  val displayName = this.toString
}

object Language {
  case object Java extends Language
  case object Scala extends Language
  case object Haskell extends Language
  case object Python extends Language

  def builtInTypes(language: Language): List[String] =
    language match {
      case Scala => List("Char", "Byte", "Short", "Int", "Long", "Float", "Double", "String")
      case Java  => List("Byte", "Boolean", "Character", "Float", "Integer", "Long", "Short", "Double", "String")
      case Python => List("bool", "float", "int", "complex", "str")
      case _     => Nil
    }
}

package com.melvic.chi.parsers

sealed trait Language {
  val displayName = this.toString

  def builtInTypes: List[String]
}

object Language {
  case object Java extends Language {
    override val builtInTypes =
      List("Byte", "Boolean", "Character", "Float", "Integer", "Long", "Short", "Double", "String")
  }

  case object Scala extends Language {
    override val builtInTypes = List("Char", "Byte", "Short", "Int", "Long", "Float", "Double", "String", "Boolean")
  }

  case object Haskell extends Language {
    override val builtInTypes = Nil // TODO: Add built-in types for Haskell
  }

  case object Python extends Language {
    override val builtInTypes = List("bool", "float", "int", "complex", "str")
  }

  case object Typescript extends Language {
    override val builtInTypes = List("boolean", "number", "bigint", "string")
  }

  lazy val allBuiltInTypes: List[String] =
    Scala.builtInTypes ++ Java.builtInTypes ++ Haskell.builtInTypes ++ Python.builtInTypes ++ Typescript.builtInTypes
}

package com.melvic.chi.parsers

sealed trait Language

object Language {
  case object Java extends Language
  case object Scala extends Language

  def builtInTypes(language: Language): List[String] = {
    val builtinTypeString = language match {
      case Scala => "Char,Byte,Short,Int,Long,Float,Double,String"
      case Java => "Byte,Boolean,Character,Float,Integer,Long,Short,Double,String"
    }
    builtinTypeString.split(",").toList
  }
}

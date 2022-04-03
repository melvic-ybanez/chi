package com.melvic.chi.parsers

sealed trait Language

object Language {
  case object Java extends Language
  case object Scala extends Language

  def builtInTypes(language: Language): List[String] = {
    val builtinTypeString = language match {
      case Scala => "Char,Byte,Short,Int,Long,Float,Double,String"
      case Java =>
        val primitive = "byte,short,int,long,float,double,boolean,char"
        val boxed = "Byte,Boolean,Char,Float,Int,Long,Short,Double,String"
        primitive + boxed
    }
    builtinTypeString.split(",").toList
  }
}

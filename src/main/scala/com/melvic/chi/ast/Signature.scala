package com.melvic.chi.ast

final case class Signature(name: String, typeParams: List[String], proposition: Proposition)

object Signature {
  def show(signature: Signature): String = {
    val typeParamsString = signature.typeParams match {
      case Nil => ""
      case typeParams => s"[${typeParams.mkString(", ")}]"
    }
    s"def ${signature.name}$typeParamsString: ${Proposition.show(signature.proposition)}"
  }
}

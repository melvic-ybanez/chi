package com.melvic.chi.ast

import com.melvic.chi.ast.Proof.Variable

final case class Signature(
    name: String,
    typeParams: List[String],
    params: List[Variable],
    proposition: Proposition
)

object Signature {
  def show(signature: Signature): String = {
    val typeParamsString = signature.typeParams match {
      case Nil        => ""
      case typeParams => s"[${typeParams.mkString(", ")}]"
    }
    val paramsString = signature.params match {
      case Nil => ""
      case params =>
        val vars = params.map { case Variable(name, proposition) =>
          s"$name: ${Proposition.show(proposition)}"
        }
        s"(${vars.mkString(", ")})"
    }

    s"def ${signature.name}$typeParamsString$paramsString: ${Proposition.show(signature.proposition)}"
  }
}

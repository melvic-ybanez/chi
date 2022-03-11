package com.melvic.chi.ast

import com.melvic.chi.ast.Proof.Variable

import scala.annotation.tailrec

final case class Signature(
    name: String,
    typeParams: List[String],
    params: List[Variable],
    proposition: Proposition
)

object Signature {
  def show(signature: Signature, split: Boolean = false): String = {
    val typeParamsString = signature.typeParams match {
      case Nil        => ""
      case typeParams => s"[${typeParams.mkString(", ")}]"
    }
    val paramsString = signature.params match {
      case Nil => ""
      case params =>
        val vars = params.map {
          case Variable(name, proposition) =>
            s"$name: ${Proposition.show(proposition)}"
        }

        val newLine = "\n  "
        val separator = if (split) "," + newLine else ", "
        val prefix = if (split) newLine else ""
        val suffix = if (split) "\n" else ""

        s"($prefix${vars.mkString(separator)}$suffix)"
    }

    s"def ${signature.name}$typeParamsString$paramsString: ${Proposition.show(signature.proposition)}"
  }
}

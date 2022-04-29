package com.melvic.chi.output

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.parsers.Language

trait Show { show =>
  def signature(signature: Signature, split: Boolean = false): String

  def proposition(proposition: Proposition): String

  def proofWithLevel(proof: Proof, level: Option[Int]): String

  def proof(proof: Proof): String = proofWithLevel(proof, None)

  def definition(signature: String, body: String, pretty: Boolean): String

  def indentWidth: Int

  def singleIndent: String = " " * indentWidth

  def indent(level: Option[Int]) = level.map(singleIndent * _).getOrElse("")

  def bodyIndent(level: Option[Int]): String = {
    val indent = this.indent(level)
    if (indent.nonEmpty) indent + singleIndent else singleIndent * 2
  }

  def paramsList(params: List[Variable], split: Boolean): String = {
    val vars = params.map {
      case Variable(name, proposition) =>
        s"$name: ${show.proposition(proposition)}"
    }
    s"(${Show.splitParams(vars, split)})"
  }
}

object Show {
  def fromLanguage(language: Language, functionName: String): Show =
    language match {
      case Language.Java    => new ShowJava
      case Language.Haskell => new ShowHaskell(functionName)
      case _                => new ShowScala
    }

  def splitParams(params: List[String], split: Boolean) = {
    val newLine = "\n  "
    val separator = if (split) "," + newLine else ", "
    val prefix = if (split) newLine else ""
    val suffix = if (split) "\n" else ""

    s"$prefix${params.mkString(separator)}$suffix"
  }

  def toCSV[A](items: List[A], separator: String = ", "): String =
    items.mkString(separator)
}

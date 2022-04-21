package com.melvic.chi.output

import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.parsers.Language

trait Display {
  def showSignature(signature: Signature, split: Boolean = false): String

  def showProposition(proposition: Proposition): String

  def showProofWithLevel(proof: Proof, level: Option[Int]): String

  def showProof(proof: Proof): String = showProofWithLevel(proof, None)

  def showDefinition(signature: String, body: String, pretty: Boolean): String

  def numberOfSpacesForIndent: Int

  def singleIndent: String = " " * numberOfSpacesForIndent

  def indent(level: Option[Int]) = level.map(singleIndent * _).getOrElse("")

  def bodyIndent(level: Option[Int]): String = {
    val indent = this.indent(level)
    if (indent.nonEmpty) indent + singleIndent else singleIndent * 2
  }
}

object Display {
  def fromLanguage(language: Language, functionName: String): Display =
    language match {
      case Language.Java    => new ShowJava
      case Language.Haskell => new ShowHaskell(functionName)
      case _                => new ShowScala
    }
}

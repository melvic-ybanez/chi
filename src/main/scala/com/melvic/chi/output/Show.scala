package com.melvic.chi.output

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.{Definition, Proposition}
import com.melvic.chi.config.Preferences
import com.melvic.chi.parsers.Language

trait Show { show =>
  implicit val prefs: Preferences

  def bodyLayouts: List[ProofLayout]

  def signature: SignatureLayout

  def prettySignature: SignatureLayout

  def proposition(proposition: Proposition): String

  def indentWidth: Int

  def line: String = "\n"

  def makeDef(signature: String, body: String): String

  def definition: DefLayout = {
    case Definition(signature, body, language) =>
      val signatureLayout = show.signature(signature)
      val signatureLayoutPretty =
        if (maxLineWidth(signatureLayout) > Preferences.maxColumn) prettySignature(signature)
        else signatureLayout

      val appliedBodyLayouts = bodyLayouts
        .map(f => f(body))
      val bodyLayout = appliedBodyLayouts
        .find(maxLineWidth(_) <= Preferences.maxColumn)
        .getOrElse(appliedBodyLayouts.last)
      makeDef(signatureLayoutPretty, bodyLayout)
  }

  def maxLineWidth(layout: String): Int = layout.split("\n").maxBy(_.length).length

  def paramsList(params: List[Variable], split: Boolean): String = {
    val vars = params.map {
      case Variable(name, proposition) =>
        s"$name: ${show.proposition(proposition)}"
    }
    s"(${Show.splitParams(vars, split, indentWidth)})"
  }

  def nestWithIndent(doc: String, i: Int): String =
    doc.replace(line, line + (" " * i))

  def nest(doc: String): String = nestWithIndent(doc, indentWidth)

  def propositionCSV(components: List[Proposition]): String =
    csv(components)(show.proposition)

  def csv[A](components: List[A])(f: A => String): String =
    Show.toCSV(components.map(f))

  def formatParams(format: Boolean): (String, Int) =
    if (format) (line, indentWidth) else ("", 0)
}

object Show {
  def fromLanguage(language: Language, functionName: String)(implicit prefs: Preferences): Show =
    language match {
      case Language.Java    => new ShowJava
      case Language.Haskell => new ShowHaskell(functionName)
      case Language.Python  => new ShowPython
      case _                => new ShowScala
    }

  def splitParams(params: List[String], split: Boolean, indent: Int = 2) = {
    val newLine = "\n" + (" " * indent)
    val separator = if (split) "," + newLine else ", "
    val prefix = if (split) newLine else ""
    val suffix = if (split) "\n" else ""

    s"$prefix${params.mkString(separator)}$suffix"
  }

  def toCSV[A](items: List[A], separator: String = ", "): String =
    items.mkString(separator)
}

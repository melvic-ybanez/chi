package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Abstraction, Conjunction, EitherCases, Variable}
import com.melvic.chi.ast.{Definition, Proof, Proposition}
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.show.Show.DefaultCSVSeparator
import com.melvic.chi.output.{DefLayout, ProofLayout, SignatureLayout}
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

  def definition: DefLayout = { case Definition(signature, body, _) =>
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

  def nestWithIndent(doc: String, i: Int): String =
    doc.replace(line, line + (" " * i))

  def nest(doc: String): String = nestWithIndent(doc, indentWidth)

  def propositionCSV(components: List[Proposition], separator: String = DefaultCSVSeparator): String =
    csv(components, separator)(show.proposition)

  def csv[A](components: List[A], separator: String = DefaultCSVSeparator)(f: A => String): String =
    Show.toCSV(components.map(f), separator)

  def formatParams(format: Boolean): (String, Int) =
    if (format) (line, indentWidth) else ("", 0)
}

object Show {
  val DefaultCSVSeparator = ", "

  def fromLanguage(language: Language, functionName: String)(implicit prefs: Preferences): Show =
    language match {
      case Language.Java       => new ShowJava
      case Language.Haskell    => new ShowHaskell(functionName)
      case Language.Python     => new ShowPython
      case Language.Typescript => new ShowTypescript
      case _                   => new ShowScala
    }

  def splitParams(params: List[String], split: Boolean, indent: Int = 2) = {
    val newLine = "\n" + (" " * indent)
    val separator = if (split) "," + newLine else ", "
    val prefix = if (split) newLine else ""
    val suffix = if (split) "\n" else ""

    s"$prefix${params.mkString(separator)}$suffix"
  }

  def toCSV[A](items: List[A], separator: String = DefaultCSVSeparator): String =
    items.mkString(separator)

  def error(reason: String = "unable to render"): String =
    "??? " + message("error", reason)

  def warning(reason: String): String =
    message("warning", reason)

  def message(label: String, reason: String): String =
    s"""/* $label: "$reason" */"""

  def useUnionNameInBranch(branch: Abstraction, unionName: Variable): Proof = {
    val Abstraction(in, out) = branch
    in match {
      case Variable(targetName, _) => Proof.rename(out, Variable.fromName(targetName) :: Nil, unionName :: Nil)
      case _                       => out
    }
  }
}

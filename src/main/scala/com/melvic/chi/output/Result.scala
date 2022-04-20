package com.melvic.chi.output

import com.melvic.chi.ast.{AssertIso, Definition}
import com.melvic.chi.config.Preferences
import com.melvic.chi.parsers.Language

object Result {
  type Result[A] = Either[Fault, A]

  def showCodeWithInfo(result: Result[Definition])(implicit preferences: Preferences): String =
    showCodeWith(result) { (code, lang) =>
      val languageString = s"Detected language: $lang"
      s"$languageString\nGenerated code:\n$code"
    }

  def showCodeWith(
      result: Result[Definition]
  )(f: (String, Language) => String)(implicit preferences: Preferences): String =
    result match {
      case Left(fault)                              => Fault.show(fault)
      case Right(code @ Definition(_, _, language)) => f(Definition.show(code), language)
    }

  /**
    * Shows only the code part (e.g. no information about the detected programming language)
    */
  def showCode(result: Result[Definition])(implicit preferences: Preferences): String =
    showCodeWith(result)((code, _) => code)

  def showIso(result: Result[(IsoResult)]): String =
    result match {
      case Left(fault) => Fault.show(fault)
      case Right(IsoResult.Fail(left, right)) =>
        s"$left is NOT isomorphic to $right"
      case Right(IsoResult.Success((leftName, leftArgs), (rightName, rightArgs))) =>
        def argsString(args: List[String]) = if (args.nonEmpty) s"[${Utils.toCSV(args)}]" else ""
        val forall = if (rightArgs.nonEmpty) s", for all types ${Utils.toCSV(leftArgs)}" else ""
        s"$leftName${argsString(leftArgs)} Is isomorphic to $rightName${argsString(rightArgs)}$forall"
    }

  def success[A](value: A): Result[A] = Right(value)

  def fail[A](fault: Fault): Result[A] = Left(fault)
}

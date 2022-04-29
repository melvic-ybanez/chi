package com.melvic.chi.output

import com.melvic.chi.ast.Definition
import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.config.Preferences
import com.melvic.chi.parsers.Language

object Result {
  type Result[A] = Either[Fault, A]

  def show[A](result: Result[A])(f: A => String): String =
    result match {
      case Left(fault) => Fault.show(fault)
      case Right(r)    => f(r)
    }

  def showCodeWithInfo(result: Result[Definition])(implicit preferences: Preferences): String =
    showCodeWith(result) { (code, lang) =>
      val languageString = s"Detected language: $lang"
      s"$languageString\nGenerated code:\n$code"
    }

  def showCodeWith(
      result: Result[Definition]
  )(f: (String, Language) => String)(implicit preferences: Preferences): String =
    show(result) {
      case code @ Definition(_, _, language) => f(Definition.show(code), language)
    }

  /**
    * Shows only the code part (e.g. no information about the detected programming language)
    */
  def showCode(result: Result[Definition])(implicit preferences: Preferences): String =
    showCodeWith(result)((code, _) => code)

  def showIso(result: Result[IsoResult]): String =
    show(result) {
      case IsoResult.Fail(left, right) =>
        s"$left is NOT isomorphic to $right"
      case IsoResult.Success((leftName, leftArgs), (rightName, rightArgs)) =>
        def argsString(args: List[String]) = if (args.nonEmpty) s"[${Show.toCSV(args)}]" else ""
        val forall = if (rightArgs.nonEmpty) s", for all types ${Show.toCSV(leftArgs)}" else ""
        s"$leftName${argsString(leftArgs)} Is isomorphic to $rightName${argsString(rightArgs)}$forall"
    }

  def showAssumption(assumption: Result[Variable]): String =
    show(assumption)(ShowAssumption.apply)

  def success[A](value: A): Result[A] = Right(value)

  def fail[A](fault: Fault): Result[A] = Left(fault)
}

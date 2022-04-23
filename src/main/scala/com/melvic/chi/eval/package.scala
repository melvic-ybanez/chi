package com.melvic.chi

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.{AssertIso, Definition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.output.Fault.ParseError
import com.melvic.chi.output.Result.Result
import com.melvic.chi.output.{IsoResult, Result}
import com.melvic.chi.parsers.{AssumptionParser, IsomorphismParser}

package object eval {
  type Evaluate = String => String

  def generateWith(signature: String)(f: Result[Definition] => String)(
      implicit prefs: Preferences,
      env: Env
  ): String =
    Generate.codeFromSignatureString(signature) match {
      case Left(error @ ParseError(_)) =>
        assertIso(signature) match {
          case Left(_) =>
            // let's keep the error for the language parsers
            val assumptionResult = assume(signature).left.map[ParseError](_ => error)

            Result.showAssumption(assumptionResult)
          case right @ Right(r) => Result.showIso(right)
        }
      case result => f(result)
    }

  def generateAndShowWithInfo(signature: String)(implicit prefs: Preferences, env: Env): String =
    generateWith(signature)(Result.showCodeWithInfo)

  def generateAndShowCode(signature: String)(implicit prefs: Preferences, env: Env): String =
    generateWith(signature)(Result.showCode)

  def assertIso(signature: String): Result[IsoResult] =
    IsomorphismParser.parseIso(signature).map {
      case AssertIso(s, s1) => Signature.isomorphic(s, s1)
    }

  def assume(assumption: String): Result[Variable] =
    AssumptionParser.parseAssumption(assumption)
}

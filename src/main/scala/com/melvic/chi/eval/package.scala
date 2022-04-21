package com.melvic.chi

import com.melvic.chi.ast.Definition
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.Fault.ParseError
import com.melvic.chi.output.Result

package object eval {
  type Evaluate = String => String

  def generateWith(signature: String)(f: Result[Definition] => String)(implicit prefs: Preferences): String =
    Generate.codeFromSignatureString(signature) match {
      case Left(error @ ParseError(_)) =>
        Result.showIso(Generate.assertIso(signature).left.map[ParseError](_ => error))
      case result => f(result)
    }

  def generateAndShowWithInfo(signature: String)(implicit prefs: Preferences): String =
    generateWith(signature)(Result.showCodeWithInfo)

  def generateAndShowCode(signature: String)(implicit prefs: Preferences): String =
    generateWith(signature)(Result.showCode)
}

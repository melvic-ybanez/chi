package com.melvic.chi

import com.melvic.chi.ast.Definition
import com.melvic.chi.config.Preferences
import com.melvic.chi.out.Result

package object eval {
  type Evaluate = String => String

  def generateWith(code: String)(f: Result[Definition] => String)(implicit prefs: Preferences): String =
    f(Generate.fromSignatureString(code))

  def generateAndShowWithInfo(code: String)(implicit prefs: Preferences): String =
    generateWith(code)(Result.showWithInfo)

  def generateAndShowCode(code: String)(implicit prefs: Preferences): String =
    generateWith(code)(Result.showCode)
}

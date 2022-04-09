package com.melvic.chi

import com.melvic.chi.config.Preferences
import com.melvic.chi.out.Result

package object eval {
  type Evaluate = String => String

  def generateAndShow(code: String)(implicit prefs: Preferences): String =
    Result.show(Generate.fromSignatureString(code))
}

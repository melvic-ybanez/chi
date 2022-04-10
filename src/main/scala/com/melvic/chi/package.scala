package com.melvic

import com.melvic.chi.config.Preferences
import com.melvic.chi.out.Result

package object chi {
  type Result[A] = Result.Result[A]
  type Evaluate = eval.Evaluate

  def generateAndShow(code: String)(implicit prefs: Preferences): String = eval.generateAndShowWithInfo(code)
}

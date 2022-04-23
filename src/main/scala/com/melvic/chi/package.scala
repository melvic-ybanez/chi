package com.melvic

import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.output.Result

package object chi {
  type Result[A] = Result.Result[A]
  type Evaluate = eval.Evaluate

  def generateAndShowWithInfo(code: String)(implicit prefs: Preferences, env: Env): String =
    eval.generateAndShowWithInfo(code)

  def generateAndShowCode(code: String)(implicit prefs: Preferences, env: Env): String =
    eval.generateAndShowCode(code)
}

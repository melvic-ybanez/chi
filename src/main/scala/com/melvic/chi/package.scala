package com.melvic

import com.melvic.chi.eval.Evaluate

package object chi {
  type Result[A] = Result.Result[A]

  def generateAndShow(code: String): String =
    Result.show(Evaluate.signatureString(code))
}

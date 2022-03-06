package com.melvic

package object chi {
  type Result[A] = Result.Result[A]

  def generateAndShow(code: String): String =
    Result.show(Evaluate.functionString(code))
}

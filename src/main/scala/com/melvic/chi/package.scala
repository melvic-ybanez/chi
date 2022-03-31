package com.melvic

import com.melvic.chi.eval.{CodeGen, Prover}
import com.melvic.chi.out.Result

package object chi {
  type Result[A] = Result.Result[A]

  def generateAndShow(code: String): String =
    Result.show(CodeGen.fromSignatureString(code))
}

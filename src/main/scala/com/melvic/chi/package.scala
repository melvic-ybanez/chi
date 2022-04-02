package com.melvic

import com.melvic.chi.eval.{CodeGen, Prover}
import com.melvic.chi.out.Result

package object chi {
  type Result[A] = Result.Result[A]
  type Evaluate = eval.Evaluate

  val generateAndShow = eval.generateAndShow
}

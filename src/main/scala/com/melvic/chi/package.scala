package com.melvic

import com.melvic.chi.output.Result

package object chi {
  type Result[A] = Result.Result[A]
  type Evaluate = eval.Evaluate
}

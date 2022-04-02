package com.melvic.chi

import com.melvic.chi.out.Result

package object eval {
  type Evaluate = String => String

  def generateAndShow: Evaluate = code =>
    Result.show(CodeGen.fromSignatureString(code))
}

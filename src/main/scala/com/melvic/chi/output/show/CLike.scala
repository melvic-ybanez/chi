package com.melvic.chi.output.show

import com.melvic.chi.output.ProofLayout

trait CLike { show: Show =>
  def proof: ProofLayout

  def bodyWithBraces: ProofLayout =
    proof => "{" + nest(line + "return " + show.proof(proof)) + ";" + line + "}"

  override def bodyLayouts = bodyWithBraces :: Nil

  override def makeDef(signature: String, body: String) =
    signature + " " + body

  override def indentWidth = 4
}

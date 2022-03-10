package com.melvic.chi.ast

final case class Definition(signature: Signature, body: Proof)

object Definition {
  def show(definition: Definition): String = {
    val bodyString = Proof.show(definition.body, None)
    val prettyBody =
      if (bodyString.length > 80) Proof.show(definition.body, Some(1))
      else "  " + bodyString
    s"${Signature.show(definition.signature)} =\n$prettyBody"
  }
}

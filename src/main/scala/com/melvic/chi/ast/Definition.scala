package com.melvic.chi.ast

import com.melvic.chi.Config

final case class Definition(signature: Signature, body: Proof)

object Definition {
  def show(definition: Definition): String = {
    val bodyString = Proof.show(definition.body, None)
    val prettyBody =
      if (bodyString.length > Config.MaxColumnWidth) Proof.show(definition.body, Some(1))
      else "  " + bodyString

    val signatureString = Signature.show(definition.signature)
    val splitSignature =
      if (signatureString.length > Config.MaxColumnWidth) Signature.show(definition.signature, split = true)
      else signatureString

    s"$splitSignature =\n$prettyBody"
  }
}

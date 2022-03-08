package com.melvic.chi.ast

final case class Definition(signature: Signature, body: Proof)

object Definition {
  def show(definition: Definition): String =
    s"${Signature.show(definition.signature)} = \n  ${Proof.show(definition.body)}"
}

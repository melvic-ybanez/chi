package com.melvic.chi.output

trait ParamsInParens {
  def signatureWithSplit(split: Boolean): SignatureLayout

  def signature: SignatureLayout = signatureWithSplit(false)

  def prettySignature: SignatureLayout = signatureWithSplit(true)
}

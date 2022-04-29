package com.melvic.chi.ast

import com.melvic.chi.config.Preferences
import com.melvic.chi.output.Show
import com.melvic.chi.parsers.Language

final case class Definition(signature: Signature, body: Proof, language: Language)

object Definition {
  def show(definition: Definition)(implicit prefs: Preferences): String = {
    val Definition(signature, body, language) = definition
    val display = Show.fromLanguage(language, signature.name)

    val bodyString = display.proof(body)
    val prettify = bodyString.length > Preferences.maxColumn
    val prettyBody =
      if (prettify) display.proofWithLevel(body, Some(1))
      else bodyString

    val signatureString = display.signature(signature)
    val splitSignature =
      if (signatureString.length > Preferences.maxColumn) display.signature(signature, split = true)
      else signatureString

    display.definition(splitSignature, prettyBody, prettify)
  }
}

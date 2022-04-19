package com.melvic.chi.ast

import com.melvic.chi.config.Preferences
import com.melvic.chi.out.Display
import com.melvic.chi.parsers.Language

final case class Definition(signature: Signature, body: Proof, language: Language)

object Definition {
  def show(definition: Definition)(implicit prefs: Preferences): String = {
    val Definition(signature, body, language) = definition
    val display = Display.fromLanguage(language, signature.name)

    val bodyString = display.showProof(body)
    val prettify = bodyString.length > Preferences.maxColumn
    val prettyBody =
      if (prettify) display.showProofWithLevel(body, Some(1))
      else bodyString

    val signatureString = display.showSignature(signature)
    val splitSignature =
      if (signatureString.length > Preferences.maxColumn) display.showSignature(signature, split = true)
      else signatureString

    display.showDefinition(splitSignature, prettyBody, prettify)
  }
}

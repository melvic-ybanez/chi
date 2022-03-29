package com.melvic.chi.ast

import com.melvic.chi.Config
import com.melvic.chi.out.Display
import com.melvic.chi.parsers.Language

final case class Definition(signature: Signature, body: Proof, language: Language)

object Definition {
  def show(definition: Definition): String = {
    val Definition(signature, body, language) = definition
    val display = Display.fromLanguage(language)

    val bodyString = display.showProof(body, None)
    val prettify = bodyString.length > Config.MaxColumnWidth
    val prettyBody =
      if (prettify) display.showProof(body, Some(1))
      else bodyString

    val signatureString = display.showSignature(signature)
    val splitSignature =
      if (signatureString.length > Config.MaxColumnWidth) display.showSignature(signature, split = true)
      else signatureString

    val languageString = s"Detected language: $language"

    s"$languageString\nGenerated code:\n${display.showDefinition(splitSignature, prettyBody, prettify)}"
  }
}

package com.melvic.chi

import com.melvic.chi.ast.Signature
import com.melvic.chi.config.Preferences

package object parsers {
  type ParseSignature = Result[(Signature, Language)]

  def parseLanguageSignature(signature: String): ParseSignature =
    JavaParser
      .parseSignature(signature)
      .orElse(ScalaParser.parseSignature(signature))

  def validInput(input: String): Boolean =
    parseLanguageSignature(input).orElse(IsomorphismParser.parseIso(input)).isRight
}

package com.melvic.chi

import com.melvic.chi.ast.Signature

package object parsers {
  type ParseSignature = Result[(Signature, Language)]

  def parseLanguageSignature(signature: String): ParseSignature =
    JavaParser
      .parseSignature(signature)
      .orElse(PythonParser.parseSignature(signature))
      .orElse(HaskellParser.parseSignature(signature))
      .orElse(ScalaParser.parseSignature(signature))

  def validateInput(input: String): Boolean =
    parseLanguageSignature(input)
      .orElse(IsomorphismParser.parseIso(input))
      .orElse(AssumptionParser.parseAssumption(input))
      .isRight
}

package com.melvic.chi

import com.melvic.chi.ast.Signature

package object parsers {
  type ParseSignature = Result[(Signature, Language)]

  /**
   * Parses the signature string using a set of available language parsers. It returns the result of the first
   * parser that succeeds, or a parse error, if all parser fail.
   *
   * Note: Since the project is biased towards Scala, we are putting the Scala parser at the end of the chain
   * here (at not at the beginning) so any parsing errors will be Scala-specific. This might change if the
   * error handling implementation will be improved in the future.
   */
  def parseLanguageSignature(signature: String): ParseSignature =
    JavaParser
      .parseSignature(signature)
      .orElse(PythonParser.parseSignature(signature))
      .orElse(HaskellParser.parseSignature(signature))
      .orElse(TypescriptParser.parseSignature(signature))
      .orElse(ScalaParser.parseSignature(signature))

  def validateInput(input: String): Boolean =
    parseLanguageSignature(input)
      .orElse(IsomorphismParser.parseIso(input))
      .orElse(AssumptionParser.parseAssumption(input))
      .isRight
}

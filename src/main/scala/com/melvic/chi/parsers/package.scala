package com.melvic.chi

import com.melvic.chi.ast.Signature
import com.melvic.chi.config.Preferences

package object parsers {
  type ParseSignature = Result[(Signature, Language)]

  def parseSignature(signature: String): ParseSignature =
    JavaParser
      .parseSignature(signature)
      .orElse(ScalaParser.parseSignature(signature))
}

package com.melvic.chi.views

import com.melvic.chi.parsers.{AssumptionParser, Language}
import com.melvic.chi.views.ChiTokenMaker.extraTokens
import org.fife.ui.rsyntaxtextarea.modes.CPlusPlusTokenMaker
import org.fife.ui.rsyntaxtextarea.{TokenMap, TokenTypes}

class ChiTokenMaker extends CPlusPlusTokenMaker {
  override def addToken(array: Array[Char], start: Int, end: Int, tokenType: Int, startOffset: Int): Unit = {
    val newTokenType =
      if (tokenType == TokenTypes.IDENTIFIER) {
        val newType = extraTokens.get(array, start, end)
        if (newType > -1) newType else tokenType
      } else tokenType
    super.addToken(array, start, end, newTokenType, startOffset)
  }
}

object ChiTokenMaker {
  lazy val extraTokens = {
    val tokens = new TokenMap(false)

    val reservedWords = List(
      AssumptionParser.AssumeOperator,
      Language.Java.displayName,
      Language.Scala.displayName,
      Language.Haskell.displayName,
      Language.Python.displayName,
      Language.Typescript.displayName,
      "def",
      "case",
      "match",
      "if",
      "while",
      "for",
      "class",
      "public",
      "protected",
      "private",
      "abstract",
      "object",
      "val",
      "var",
      "import",
      "trait",
      "lazy",
      "return",
      "of",
      "isinstance",
      "lambda",
      "function"
    )
    val functions = List("identity", "const", "compose", "andThen", "apply", "_1", "_2", "_3")
    val dataTypes = Language.allBuiltInTypes ++ List(
      "Function",
      "BiFunction",
      "Either",
      "Left",
      "Right",
      "Tuple",
      "Union",
      "Callable"
    )

    reservedWords.foreach(tokens.put(_, TokenTypes.RESERVED_WORD))
    functions.foreach(tokens.put(_, TokenTypes.FUNCTION))
    dataTypes.foreach(tokens.put(_, TokenTypes.DATA_TYPE))

    tokens
  }
}

package com.melvic.chi.parsers

object Utils {
  def removeComments(definitions: List[String]): List[String] =
    definitions
      .map(_.trim)
      .filterNot(_.startsWith("//"))
      // for comments appearing at the end of the definition
      .map { str =>
        val i = str.indexOf("//")
        if (i != -1) str.take(i) else str
      }
}

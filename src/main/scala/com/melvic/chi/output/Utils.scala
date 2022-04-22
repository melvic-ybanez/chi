package com.melvic.chi.output

object Utils {
  def splitParams(params: List[String], split: Boolean) = {
    val newLine = "\n  "
    val separator = if (split) "," + newLine else ", "
    val prefix = if (split) newLine else ""
    val suffix = if (split) "\n" else ""

    s"$prefix${params.mkString(separator)}$suffix"
  }

  def toCSV[A](items: List[A], separator: String = ", "): String =
    items.mkString(separator)
}

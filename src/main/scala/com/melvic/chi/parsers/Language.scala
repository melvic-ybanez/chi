package com.melvic.chi.parsers

sealed trait Language

object Language {
  case object Java extends Language
  case object Scala extends Language
}

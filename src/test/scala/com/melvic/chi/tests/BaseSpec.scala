package com.melvic.chi.tests

import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.parsers.Language
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait BaseSpec extends AnyFlatSpec with should.Matchers {
  val language: Language

  implicit val preferences: Preferences = Preferences.loadDefaults
  implicit val env: Env = Env.default

  def output(outputString: String): String = BaseSpec.output(outputString, language)
}

object BaseSpec {
  def output(outputString: String, language: Language): String =
    s"""Detected language: ${language.displayName}
       |Generated code:
       |$outputString""".stripMargin
}

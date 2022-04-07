package com.melvic.chi.tests

import com.melvic.chi.config.Preferences
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait BaseSpec extends AnyFlatSpec with should.Matchers {
  val language: String

  implicit val preferences: Preferences = Preferences.load

  def output(outputString: String): String =
    s"""Detected language: $language
       |Generated code:
       |$outputString""".stripMargin
}

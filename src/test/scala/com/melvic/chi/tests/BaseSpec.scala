package com.melvic.chi.tests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait BaseSpec extends AnyFlatSpec with should.Matchers {
  val language: String

  def output(outputString: String): String =
    s"""Detected language: $language
       |Generated code:
       |$outputString""".stripMargin
}

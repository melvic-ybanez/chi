package com.melvic.chi.tests

import com.melvic.chi.config.Preferences
import com.melvic.chi.eval.Generate
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.io.Source

class FileContentSpec extends AnyFlatSpec with should.Matchers {
  implicit val preferences: Preferences = Preferences.loadDefaults

  def readFileContent(path: String): List[String] =
    Source.fromResource("tests/" + path).getLines.toList

  "file content" should "support multiple inputs from different languages" in {

    Generate.allToString(readFileContent("languages_demo_input.chi")) should be(
      readFileContent("languages_demo_output.chi").mkString("\n")
    )
  }

  it should "support isomorphism queries" in {
    Generate.allToString(readFileContent("isomorphisms_input.chi")) should be(
      readFileContent("isomorphisms_output.chi").mkString("\n")
    )
  }

  it should "support assumptions" in {
    Generate.allToString(readFileContent("assumptions_input.chi")) should be(
      readFileContent("assumptions_output.chi").mkString("\n")
    )
  }
}

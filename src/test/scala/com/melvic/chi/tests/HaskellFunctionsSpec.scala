package com.melvic.chi.tests

import com.melvic.chi.generateAndShowWithInfo

class HaskellFunctionsSpec extends BaseSpec {
  val language = "Haskell"

  def test(input: String, outputBody: String) =
    generateAndShowWithInfo(input) should be(output(s"$input\n$outputBody"))

  "A => A" should "map the input to itself" in {
    test("id :: a -> a", "id a = a")
  }

  "(A => B) => A => B" should "apply the function to the input of the resulting function" in {
    test("apply :: (a -> b) -> a -> b", "apply f a = f a")
  }

  "fst" should "return the first element" in {
    test("fst :: (a, b) -> a", "fst (a, b) = a")
  }
}

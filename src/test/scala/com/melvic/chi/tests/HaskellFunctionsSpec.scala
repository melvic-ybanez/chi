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

  "snd" should "return the second element" in {
    test("snd :: (a, b) -> b", "snd (a, b) = b")
  }

  "compose" should "apply the first function after the second" in {
    test("compose :: (b -> c) -> (a -> b) -> a -> c", "compose f g a = f (g a)")
  }

  "andThen" should "apply the first function before the second" in {
    test("andThen :: (a -> b) -> (b -> c) -> a -> c", "andThen f g a = g (f a)")
  }

  "unit" should "be provable with the universal value ()" in {
    test("unit :: ()", "unit = ()")
  }

  "conjunction" should "depend on the proofs of its components" in {
    test("foo :: a -> (b -> (a, b)) -> b -> (a, b)", "foo a f b = (a, b)")
  }

  "either" should "default to left when the evaluation succeeds" in {
    test("left :: a -> Either a a", "left a = Left a")
    test("left :: a -> Either a b", "left a = Left a")
    test("right :: b -> Either a b", "right b = Right b")
  }

  "all assumptions" should "be considered" in {
    test("foo :: (a -> c) -> (b -> c) -> b -> c", "foo f g b = g b")
    test("foo :: (b -> c) -> (a -> c) -> b -> c", "foo f g b = f b")
  }

  "implication" should "evaluate it's antecedent recursive" in {
    test("foo :: (a -> b) -> ((a -> b) -> c) -> c", "foo f g = g (\\a -> f a)")
  }

  "disjunction elimination" should "work as formalized in propositional logic" in {
    generateAndShowWithInfo("foo :: (a -> c) -> (b -> c) -> Either a b -> c") should be(
      output(
        """foo :: (a -> c) -> (b -> c) -> Either a b -> c
          |foo f g e = case e of
          |    Left a -> f a
          |    Right b -> g b""".stripMargin
      )
    )

    generateAndShowWithInfo("foo :: Either a a -> a") should be(
      output(
        """foo :: Either a a -> a
          |foo e = case e of
          |    Left a -> a
          |    Right a -> a""".stripMargin
      )
    )
  }

  "either over tuples" should "deconstruct the tuples" in {
    generateAndShowWithInfo("foo :: Either (a, b) a -> a") should be(
      output(
        """foo :: Either (a, b) a -> a
          |foo e = case e of
          |    Left (a, b) -> a
          |    Right a -> a""".stripMargin
      )
    )
  }
}

package com.melvic.chi.tests

import com.melvic.chi.generateAndShow
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ScalaDefSpec extends AnyFlatSpec with should.Matchers {
  "A => A" should "map the input to itself" in {
    generateAndShow("def identity[A]: A => A") should be(
      """Detected language: Scala
        |Generated code:
        |def identity[A]: (A => A) =
        |  a => a""".stripMargin
    )
    generateAndShow("def identity[A](a: A): A") should be(
      """Detected language: Scala
        |Generated code:
        |def identity[A](a: A): A =
        |  a""".stripMargin
    )
  }

  "(A => B) => A => B" should "apply the function to the input of the resulting function" in {
    generateAndShow("def apply[A, B]: (A => B) => A => B") should be(
      """Detected language: Scala
        |Generated code:
        |def apply[A, B]: ((A => B) => (A => B)) =
        |  f => a => f(a)""".stripMargin
    )
  }

  "fst" should "return the first element" in {
    generateAndShow("def fst[A, B]: (A, B) => A") should be(
      """Detected language: Scala
        |Generated code:
        |def fst[A, B]: ((A, B) => A) =
        |  { case (a, b) =>
        |    a
        |  }""".stripMargin
    )
  }

  "snd" should "return the second element" in {
    generateAndShow("def snd[A, B]: (A, B) => B") should be(
      """Detected language: Scala
        |Generated code:
        |def snd[A, B]: ((A, B) => B) =
        |  { case (a, b) =>
        |    b
        |  }""".stripMargin
    )
  }

  "compose" should "apply the first function after the second" in {
    generateAndShow("def compose[A, B, C]: (B => C) => (A => B) => A => C") should be(
      """Detected language: Scala
        |Generated code:
        |def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
        |  f => g => a => f(g(a))""".stripMargin
    )
  }

  "andThen" should "apply the first function before the second" in {
    generateAndShow("def andThen[A, B, C]: (A => B) => (B => C) => A => C") should be(
      """Detected language: Scala
        |Generated code:
        |def andThen[A, B, C]: ((A => B) => ((B => C) => (A => C))) =
        |  f => g => a => g(f(a))""".stripMargin
    )
    generateAndShow("def andThen[A, B, C](f: (A => B), g: (B => C)): A => C") should be(
      """Detected language: Scala
        |Generated code:
        |def andThen[A, B, C](f: (A => B), g: (B => C)): (A => C) =
        |  a => g(f(a))""".stripMargin
    )
  }

  "unit" should "be provable with the universal value ()" in {
    generateAndShow("def unit: ()") should be(
      """Detected language: Scala
        |Generated code:
        |def unit: () =
        |  ()""".stripMargin
    )
    generateAndShow("def unit[A]: (() => A) => A") should be(
      """Detected language: Scala
        |Generated code:
        |def unit[A]: ((() => A) => A) =
        |  f => f()""".stripMargin
    )
  }

  "conjunction" should "depend on the proofs of its components" in {
    generateAndShow("def foo[A, B]: A => (B => (A, B)) => B => (A, B)") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B]: (A => ((B => (A, B)) => (B => (A, B)))) =
        |  a => f => b => (a, b)""".stripMargin
    )
  }

  "either" should "default to left when the evaluation succeeds" in {
    generateAndShow("def left[A]: A => Either[A, A]") should be(
      """Detected language: Scala
        |Generated code:
        |def left[A]: (A => Either[A, A]) =
        |  a => Left(a)""".stripMargin
    )

    generateAndShow("def left[A, B]: A => Either[A, B]") should be(
      """Detected language: Scala
        |Generated code:
        |def left[A, B]: (A => Either[A, B]) =
        |  a => Left(a)""".stripMargin
    )

    generateAndShow("def right[A, B]: B => Either[A, B]") should be(
      """Detected language: Scala
        |Generated code:
        |def right[A, B]: (B => Either[A, B]) =
        |  b => Right(b)""".stripMargin
    )
  }

  "all assumptions" should "be considered" in {
    generateAndShow("def foo[A, B, C]: (A => C) => (B => C) => B => C") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: ((A => C) => ((B => C) => (B => C))) =
        |  f => g => b => g(b)""".stripMargin
    )

    generateAndShow("def foo[A, B, C]: (B => C) => (A => C) => B => C") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: ((B => C) => ((A => C) => (B => C))) =
        |  f => g => b => f(b)""".stripMargin
    )
  }

  "implication" should "evaluate it's antecedent recursive" in {
    // Note: `a => f(a)` could have been simplified to just `f`
    generateAndShow("def foo[A, B, C]: (A => B) => ((A => B) => C) => C") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: ((A => B) => (((A => B) => C) => C)) =
        |  f => g => g(a => f(a))""".stripMargin
    )
  }

  "Unknown propositions" should "not be allowed" in {
    generateAndShow("def foo[A]: A => B") should be(
      "Unknown propositions: B"
    )
  }

  "disjunction elimination" should "work as formalized in propositional logic" in {
    generateAndShow("def foo[A, B, C]: (A => C) => (B => C) => Either[A, B] => C") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: ((A => C) => ((B => C) => (Either[A, B] => C))) =
        |  f => g => e => e match {
        |    case Left(a) => f(a)
        |    case Right(b) => g(b)
        |  }""".stripMargin
    )

    generateAndShow("def foo[A, B]: Either[A, A] => A") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B]: (Either[A, A] => A) =
        |  e => e match {
        |    case Left(a) => a
        |    case Right(a) => a
        |  }""".stripMargin
    )

    generateAndShow("def foo[A, B, C]: Either[A => C, B] => (B => C) => A => C") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: (Either[(A => C), B] => ((B => C) => (A => C))) =
        |  e => f => a => e match {
        |    case Left(g) => g(a)
        |    case Right(b) => f(b)
        |  }""".stripMargin
    )
  }

  "search for implication assumption" should "be recursive" in {
    generateAndShow("def foo[A, B, C]: (A => (B => C)) => ((A, B) => C)") should be(
      """Detected language: Scala
        |Generated code:
        |def foo[A, B, C]: ((A => (B => C)) => ((A, B) => C)) =
        |  f => { case (a, b) =>
        |    f(a)(b)
        |  }""".stripMargin
    )
  }

  "Built-in types" should "be included in the type search" in {
    generateAndShow("def foo(f: String => Int, g: Float => Int): Either[String, Float] => Int") should be(
      """Detected language: Scala
        |Generated code:
        |def foo(f: (String => Int), g: (Float => Int)): (Either[String, Float] => Int) =
        |  e => e match {
        |    case Left(s) => f(s)
        |    case Right(h) => g(h)
        |  }""".stripMargin
    )
  }
}

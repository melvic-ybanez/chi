package com.melvic.chi.tests

import com.melvic.chi.{Evaluate, generateAndShow}
import com.melvic.chi.Result.Result
import com.melvic.chi.ast.{Definition, Proof}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class UniversallyQuantifiedSpec extends AnyFlatSpec with should.Matchers {
  "A => A" should "map the input to itself" in {
    generateAndShow("def identity[A]: A => A") should be(
      """def identity[A]: (A => A) =
        |  a => a""".stripMargin
    )
    generateAndShow("def identity[A](a: A): A") should be(
      """def identity[A](a: A): A =
        |  a""".stripMargin
    )
  }

  "fst" should "return the first element" in {
    generateAndShow("def fst[A, B]: (A, B) => A") should be(
      """def fst[A, B]: ((A, B) => A) =
        |  { case (a, b) =>
        |    a
        |  }""".stripMargin
    )
  }

  "snd" should "return the second element" in {
    generateAndShow("def snd[A, B]: (A, B) => B") should be(
      """def snd[A, B]: ((A, B) => B) =
        |  { case (a, b) =>
        |    b
        |  }""".stripMargin
    )
  }

  "compose" should "apply the first function after the second" in {
    generateAndShow("def compose[A, B, C]: (B => C) => (A => B) => A => C") should be(
      """def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
        |  f => g => a => f(g(a))""".stripMargin
    )
  }
}

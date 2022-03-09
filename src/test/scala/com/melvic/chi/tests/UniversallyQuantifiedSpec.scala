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
}

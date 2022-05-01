package com.melvic.chi.tests

import com.melvic.chi.Repl
import com.melvic.chi.config.Preferences
import com.melvic.chi.parsers.Language
import com.melvic.chi.tests.BaseSpec.output
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ReplSpec extends AnyFlatSpec with should.Matchers {
  def test(inputs: Iterator[String], outputs: Iterator[String]): Unit = {
    var result: String = ""

    val print: Repl.Print = printed =>
      // perform assertion after each print, but do this only if
      // the output to print is not empty and is likely not the
      // welcome message
      if (printed.nonEmpty && !printed.startsWith("Welcome"))
        printed should be(result)
    val readLine: Repl.ReadLine = { _ =>
      result = outputs.next()
      inputs.next()
    }

    Repl.start(print, readLine)(Preferences.loadDefaults)
  }

  "Repl" should "generate correct code for the supported languages" in {
    val inputs = Iterator(
      "def apply[A, B]: (A => B) => A => B", // Scala
      "<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g)", // Java
      "foo :: Either (a, b) a -> a ", // Haskell
      "exit"
    )

    val outputs = Iterator(
      output(
        """def apply[A, B]: (A => B) => A => B =
          |  identity""".stripMargin,
        Language.Scala
      ),
      output(
        """<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {
          |    return a -> f.apply(g.apply(a));
          |}""".stripMargin,
        Language.Java
      ),
      output(
        """foo :: Either (a, b) a -> a
          |foo e = case e of
          |  Left (a, b) -> a
          |  Right a -> a""".stripMargin,
        Language.Haskell
      ),
      "Bye!"
    )

    test(inputs, outputs)
  }

  it should "be able to check for isomorphism" in {
    val inputs = Iterator(
      "def foo[A]: A => A <=> def bar[B]: B => B",
      "def foo: String <=> def bar: Int",
      "exit"
    )
    val outputs = Iterator(
      "foo[C] Is isomorphic to bar[C], for all types C",
      "foo is NOT isomorphic to bar",
      "Bye!"
    )
    test(inputs, outputs)
  }

  it should "support declaration of assumptions" in {
    val inputs = Iterator(
      "assume either: A | B",
      "def foo[A, B, C]: (A => C) => (B => C) => C",
      "exit"
    )
    val outputs = Iterator(
      "Assume either: A | B",
      output(
        """def foo[A, B, C]: (A => C) => (B => C) => C =
          |  f => g => either match {
          |    case Left(a) => f(a)
          |    case Right(b) => g(b)
          |  }""".stripMargin,
        Language.Scala
      ),
      "Bye!"
    )
    test(inputs, outputs)
  }
}

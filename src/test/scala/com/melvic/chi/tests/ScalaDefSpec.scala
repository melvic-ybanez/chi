package com.melvic.chi.tests

import com.melvic.chi.generateAndShow

class ScalaDefSpec extends BaseSpec {
  val language = "Scala"

  "A => A" should "map the input to itself" in {
    generateAndShow("def identity[A]: A => A") should be(
      output(
        """def identity[A]: (A => A) =
          |  Predef.identity""".stripMargin
      )
    )
    generateAndShow("def identity[A](a: A): A") should be(
      output(
        """def identity[A](a: A): A =
          |  a""".stripMargin
      )
    )
  }

  "(A => B) => A => B" should "apply the function to the input of the resulting function" in {
    generateAndShow("def apply[A, B](f: A => B, a: A): B") should be(
      output(
        """def apply[A, B](f: (A => B), a: A): B =
        |  f(a)""".stripMargin
      )
    )

    generateAndShow("def apply[A, B]: (A => B) => A => B") should be(
      output(
        """def apply[A, B]: ((A => B) => (A => B)) =
          |  identity""".stripMargin
      )
    )
  }

  "fst" should "return the first element" in {
    generateAndShow("def fst[A, B]: (A, B) => A") should be(
      output(
        """def fst[A, B]: ((A, B) => A) =
          |  { case (a, b) =>
          |    a
          |  }""".stripMargin
      )
    )
  }

  "snd" should "return the second element" in {
    generateAndShow("def snd[A, B]: (A, B) => B") should be(
      output(
        """def snd[A, B]: ((A, B) => B) =
          |  { case (a, b) =>
          |    b
          |  }""".stripMargin
      )
    )
  }

  "compose" should "apply the first function after the second" in {
    generateAndShow("def compose[A, B, C]: (B => C) => (A => B) => A => C") should be(
      output(
        """def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
          |  f => g => f.compose(g)""".stripMargin
      )
    )
  }

  "andThen" should "apply the first function before the second" in {
    generateAndShow("def andThen[A, B, C]: (A => B) => (B => C) => A => C") should be(
      output(
        """def andThen[A, B, C]: ((A => B) => ((B => C) => (A => C))) =
          |  f => g => g.compose(f)""".stripMargin
      )
    )
    generateAndShow("def andThen[A, B, C](f: (A => B), g: (B => C)): A => C") should be(
      output(
        """def andThen[A, B, C](f: (A => B), g: (B => C)): (A => C) =
          |  g.compose(f)""".stripMargin
      )
    )
  }

  "unit" should "be provable with the universal value ()" in {
    generateAndShow("def unit: ()") should be(
      output(
        """def unit: () =
          |  ()""".stripMargin
      )
    )
    generateAndShow("def unit[A]: (() => A) => A") should be(
      output(
        """def unit[A]: ((() => A) => A) =
          |  f => f()""".stripMargin
      )
    )
  }

  "conjunction" should "depend on the proofs of its components" in {
    generateAndShow("def foo[A, B]: A => (B => (A, B)) => B => (A, B)") should be(
      output(
        """def foo[A, B]: (A => ((B => (A, B)) => (B => (A, B)))) =
          |  a => f => b => (a, b)""".stripMargin
      )
    )
  }

  "either" should "default to left when the evaluation succeeds" in {
    generateAndShow("def left[A]: A => Either[A, A]") should be(
      output(
        """def left[A]: (A => Either[A, A]) =
          |  a => Left(a)""".stripMargin
      )
    )

    generateAndShow("def left[A, B]: A => Either[A, B]") should be(
      output(
        """def left[A, B]: (A => Either[A, B]) =
          |  a => Left(a)""".stripMargin
      )
    )

    generateAndShow("def right[A, B]: B => Either[A, B]") should be(
      output(
        """def right[A, B]: (B => Either[A, B]) =
          |  b => Right(b)""".stripMargin
      )
    )
  }

  "all assumptions" should "be considered" in {
    generateAndShow("def foo[A, B, C]: (A => C) => (B => C) => B => C") should be(
      output(
        """def foo[A, B, C]: ((A => C) => ((B => C) => (B => C))) =
          |  f => identity""".stripMargin
      )
    )

    generateAndShow("def foo[A, B, C]: (B => C) => (A => C) => B => C") should be(
      output(
        """def foo[A, B, C]: ((B => C) => ((A => C) => (B => C))) =
          |  f => g => f""".stripMargin
      )
    )
  }

  "implication" should "evaluate it's antecedent recursive" in {
    generateAndShow("def foo[A, B, C]: (A => B) => ((A => B) => C) => C") should be(
      output(
        """def foo[A, B, C]: ((A => B) => (((A => B) => C) => C)) =
          |  f => g => g(f)""".stripMargin
      )
    )
  }

  "Unknown propositions" should "not be allowed" in {
    generateAndShow("def foo[A]: A => B") should be(
      "Unknown propositions: B"
    )
  }

  "disjunction elimination" should "work as formalized in propositional logic" in {
    generateAndShow("def foo[A, B, C]: (A => C) => (B => C) => Either[A, B] => C") should be(
      output(
        """def foo[A, B, C]: ((A => C) => ((B => C) => (Either[A, B] => C))) =
          |  f => g => e => e match {
          |    case Left(a) => f(a)
          |    case Right(b) => g(b)
          |  }""".stripMargin
      )
    )

    generateAndShow("def foo[A, B]: Either[A, A] => A") should be(
      output(
        """def foo[A, B]: (Either[A, A] => A) =
          |  e => e match {
          |    case Left(a) => a
          |    case Right(a) => a
          |  }""".stripMargin
      )
    )

    generateAndShow("def foo[A, B, C]: Either[A => C, B] => (B => C) => A => C") should be(
      output(
        """def foo[A, B, C]: (Either[(A => C), B] => ((B => C) => (A => C))) =
          |  e => f => a => e match {
          |    case Left(g) => g(a)
          |    case Right(b) => f(b)
          |  }""".stripMargin
      )
    )
  }

  "search for implication assumption" should "be recursive" in {
    generateAndShow("def foo[A, B, C]: (A => (B => C)) => ((A, B) => C)") should be(
      output(
        """def foo[A, B, C]: ((A => (B => C)) => ((A, B) => C)) =
          |  f => { case (a, b) =>
          |    f(a)(b)
          |  }""".stripMargin
      )
    )
  }

  "Built-in types" should "be included in the type search" in {
    generateAndShow("def foo(f: String => Int, g: Float => Int): Either[String, Float] => Int") should be(
      output(
        """def foo(f: (String => Int), g: (Float => Int)): (Either[String, Float] => Int) =
          |  e => e match {
          |    case Left(s) => f(s)
          |    case Right(h) => g(h)
          |  }""".stripMargin
      )
    )
  }

  "Function expressions" should "be simplified" in {
    generateAndShow("def foo(f: String => Int): String => Int") should be(
      output(
        """def foo(f: (String => Int)): (String => Int) =
          |  f""".stripMargin
      )
    )

    generateAndShow("def foo[A, B, C]: A => (A => (B => C)) => B => C") should be(
      output(
        """def foo[A, B, C]: (A => ((A => (B => C)) => (B => C))) =
          |  a => f => f(a)""".stripMargin
      )
    )
  }

  "Point-free style" should "use Scala's `compose` function" in {
    generateAndShow("def compose(f: String => Int, g: Int => Float): Double => String => Float") should be(
      output(
        """def compose(
          |  f: (String => Int),
          |  g: (Int => Float)
          |): (Double => (String => Float)) =
          |  d => g.compose(f)""".stripMargin
      )
    )

    generateAndShow("def foo(f: String => Int, g: Double => Int => Float, d: Double): String => Float") should be(
      output(
        """def foo(
          |  f: (String => Int),
          |  g: (Double => (Int => Float)),
          |  d: Double
          |): (String => Float) =
          |  g(d).compose(f)""".stripMargin
      )
    )
  }
}

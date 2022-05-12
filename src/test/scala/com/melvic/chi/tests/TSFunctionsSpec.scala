package com.melvic.chi.tests

import com.melvic.chi.eval.generateAndShowWithInfo
import com.melvic.chi.parsers.Language

class TSFunctionsSpec extends BaseSpec {
  override val language = Language.Typescript

  "identity" should "map the input to itself" in {
    generateAndShowWithInfo("function id<A>(a: A): A") should be(
      output(
        """function id<A>(a: A): A  {
          |    return a;
          |}""".stripMargin
      )
    )
  }

  "(A => B) => A => B" should "apply the function to the input of the resulting function" in {
    generateAndShowWithInfo("function apply<A, B>(f: (a: A) => B, a: A): B") should be(
      output(
        """function apply<A, B>(f: (a: A) => B, a: A): B  {
          |    return f(a);
          |}""".stripMargin
      )
    )
  }

  "fst" should "return the first element" in {
    generateAndShowWithInfo("function fst<A, B>(pair: [A, B]): A") should be(
      output(
        """function fst<A, B>(pair: [A, B]): A  {
          |    return (() => {
          |        // Note: This is verbose for compatibility reasons
          |        const [a, b] = pair
          |        return a
          |    })();
          |}""".stripMargin
      )
    )
  }

  "compose" should "apply the first function after the second" in {
    generateAndShowWithInfo("function compose<A, B, C>(f: (b: B) => C, g: (a: A) => B): (a: A) => C") should be(
      output(
        """function compose<A, B, C>(f: (b: B) => C, g: (a: A) => B): (a: A) => C  {
          |    return (a: A) => f(g(a));
          |}""".stripMargin
      )
    )
  }
}

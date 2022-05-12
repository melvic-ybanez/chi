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

  "conjunction" should "depend on the proofs of its components" in {
    generateAndShowWithInfo("function foo<A, B>(a: A, f: (b: B) => [A, B], b: B): [A, B]") should be(
      output(
        """function foo<A, B>(a: A, f: (b: B) => [A, B], b: B): [A, B]  {
          |    return [a, b];
          |}""".stripMargin
      )
    )
  }

  "union" should "default to left when the evaluation succeeds" in {
    generateAndShowWithInfo("function left(str: string): string | string") should be(
      output(
        """function left(str: string): string | string  {
          |    return str;
          |}""".stripMargin
      )
    )
  }

  "all assumptions" should "be considered" in {
    generateAndShowWithInfo("\nfunction foo<A, B, C>(f: (a: A) => C, g: (b: B) => C): (b: B) => C") should be(
      output(
        """function foo<A, B, C>(f: (a: A) => C, g: (b: B) => C): (b: B) => C  {
          |    return (b: B) => g(b);
          |}""".stripMargin
      )
    )
  }

  "implication" should "evaluate its antecedent recursively" in {
    generateAndShowWithInfo("function foo<A, B, C>(f: (a: A) => B, g: (h: (a: A) => B) => C): C") should be(
      output(
        """function foo<A, B, C>(f: (a: A) => B, g: (h: (a: A) => B) => C): C  {
          |    return g((a: A) => f(a));
          |}""".stripMargin
      )
    )
  }

  "disjunction elimination" should "work as formalized in propositional logic" in {
    generateAndShowWithInfo(
      """function foo(
        |	f: (s: string) => boolean,
        |	g: (n: number) => boolean
        |): (either: string | number) => boolean""".stripMargin
    ) should be(
      output(
        """function foo(
          |    f: (s: string) => boolean,
          |    g: (n: number) => boolean
          |): (either: string | number) => boolean  {
          |    return (e: string | number) => (() => {
          |        if (typeof(e) === 'string')
          |            return f(e)
          |        else return g(e)
          |    })();
          |}""".stripMargin
      )
    )

    generateAndShowWithInfo(
      """function foo(
        |	u: string | number | boolean,
        |	f: (s: string) => boolean,
        |	g: (n: number) => boolean,
        |	h: (b: boolean) => boolean
        |): boolean""".stripMargin
    ) should be(
      output(
        """function foo(
          |    u: number | boolean | string,
          |    f: (s: string) => boolean,
          |    g: (n: number) => boolean,
          |    h: (b: boolean) => boolean
          |): boolean  {
          |    return (() => {
          |        if (typeof(u) === 'string')
          |            return f(u)
          |        else return (() => {
          |            if (typeof(u) === 'number')
          |                return g(u)
          |            else return u
          |        })()
          |    })();
          |}""".stripMargin
      )
    )
  }

  "component of a product consequent" should "be accessible if the function is applied" in {
    generateAndShowWithInfo("function foo<A, B, C>(f: (a: A) => [C, B], a: A): B") should be(
      output(
        """function foo<A, B, C>(f: (a: A) => [C, B], a: A): B  {
          |    return f(a)[1];
          |}""".stripMargin
      )
    )

    generateAndShowWithInfo("function foo<A, B>(f: (a: A) => [A, B]): [(a: A) => A, (a: A) => B]") should be(
      output(
        """function foo<A, B>(f: (a: A) => [A, B]): [(a: A) => A, (a: A) => B]  {
          |    return [(a: A) => a, (a: A) => f(a)[1]];
          |}""".stripMargin
      )
    )
  }
}

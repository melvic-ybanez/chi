package com.melvic.chi.tests

import com.melvic.chi.eval.generateAndShowWithInfo
import com.melvic.chi.parsers.Language

class JavaFunctionsSpec extends BaseSpec {
  val language = Language.Java

  "function application" should "work according to the implication elimination rule" in {
    generateAndShowWithInfo("<A, B> B apply(A a, Function<A, B> f)") should be(
      output(
        """<A, B> B apply(A a, Function<A, B> f) {
          |    return f.apply(a);
          |}""".stripMargin
      )
    )
  }

  "function composition" should "chain functions with aligned types" in {
    generateAndShowWithInfo("<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g)") should be(
      output(
        """<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {
          |    return a -> f.apply(g.apply(a));
          |}""".stripMargin
      )
    )
  }

  "function interfaces" should "support lambda notations from Java 8 and beyond" in {
    generateAndShowWithInfo("<A, B, C> BiFunction<A, B, C> foo(Function<A, C> f)") should be(
      output(
        """<A, B, C> BiFunction<A, B, C> foo(Function<A, C> f) {
          |    return (a, b) -> {
          |        return f.apply(a);
          |    };
          |}""".stripMargin
      )
    )
  }

  "Built-in types" should "be included in the type search" in {
    generateAndShowWithInfo("String idString(String s)") should be(
      output(
        """String idString(String s) {
          |    return s;
          |}""".stripMargin
      )
    )
    generateAndShowWithInfo("Function<String, Integer> bar(Function<String, Float> f, Function<Float, Integer> g)") should be(
      output(
        """Function<String, Integer> bar(
          |  Function<String, Float> f,
          |  Function<Float, Integer> g
          |) {
          |    return s -> g.apply(f.apply(s));
          |}""".stripMargin
      )
    )
  }
}

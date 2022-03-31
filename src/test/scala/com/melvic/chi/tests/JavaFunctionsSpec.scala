package com.melvic.chi.tests

import com.melvic.chi.generateAndShow
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class JavaFunctionsSpec extends AnyFlatSpec with should.Matchers {
  "function application" should "work according to the implication elimination rule" in {
    generateAndShow("<A, B> B apply(A a, Function<A, B> f)") should be(
      """Detected language: Java
        |Generated code:
        |<A, B> B apply(A a, Function<A, B> f) {
        |    return f.apply(a);
        |}""".stripMargin
    )
  }

  "function composition" should "chain functions with aligned types" in {
    generateAndShow("<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g)") should be(
      """Detected language: Java
        |Generated code:
        |<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {
        |    return a -> f.apply(g.apply(a));
        |}""".stripMargin
    )
  }

  "function interfaces" should "support lambda notations from Java 8 and beyond" in {
    generateAndShow("<A, B, C> BiFunction<A, B, C> foo(Function<A, C> f)") should be(
      """Detected language: Java
        |Generated code:
        |<A, B, C> BiFunction<A, B, C> foo(Function<A, C> f) {
        |    return (a, b) -> {
        |        return f.apply(a);
        |    };
        |}""".stripMargin
    )
  }
}

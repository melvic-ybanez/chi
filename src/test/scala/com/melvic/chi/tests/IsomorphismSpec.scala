package com.melvic.chi.tests

import com.melvic.chi.config.Preferences
import com.melvic.chi.eval.{Generate, generateAndShowCode, generateWith}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class IsomorphismSpec extends AnyFlatSpec with should.Matchers {
  def iso(leftName: String, leftArgs: String, rightName: String, rightArgs: String, typeArgs: String): String = {
    def argsString(args: String) = if (args.nonEmpty) s"[$args]" else ""
    val forall = if (typeArgs.nonEmpty) s", for all types $typeArgs" else ""
    s"$leftName${argsString(leftArgs)} Is isomorphic to $rightName${argsString(rightArgs)}$forall"
  }

  def notIso(leftName: String, rightName: String): String =
    s"$leftName is NOT isomorphic to $rightName"

  implicit val preferences: Preferences = Preferences.loadDefaults

  def generate(signatureString: String) = generateAndShowCode(signatureString)

  "isomorphism" should "exist between two identities" in {
    generate("def foo[A]: A => A <=> def bar[B]: B => B") should be(
      iso("foo", "C", "bar", "C", "C")
    )
  }

  it should "exist between alpha-equivalent functions" in {
    generate("def foo[A, B]: A => (A, B) <=> def bar[C, D]: C => (C, D)") should be(
      iso("foo", "F, E", "bar", "F, E", "F, E")
    )
  }

  it should "consider the laws of exponents" in {
    generate("def foo[A, B]: A => (A, B) <=> def bar[A, B]: (B => B, B => A)") should be(
      iso("foo", "D, C", "bar", "C, D", "D, C")
    )
    generate("def f[A, B, C]: (A => C, B => C) <=> def g[A, B, C]: Either[A, B] => C") should be(
      iso("f", "F, E, D", "g", "F, E, D", "F, E, D")
    )
  }

  it should "respect conjunction associativity" in {
    generate("def foo[A]: (A, (A, A)) <=> def bar[B]: (B, B, B)") should be(
      iso("foo", "C", "bar", "C", "C")
    )
    generate("def foo[A]: A => (A, (A, A)) <=> def bar[B]: B => (B, B, B)") should be(
      iso("foo", "C", "bar", "C", "C")
    )
  }

  it should "respect disjunction associativity" in {
    generate("def foo: String => Int <=> def bar: String => Int") should be(
      iso("foo", "", "bar", "", "")
    )
  }

  it should "exist between disjunction that differ only in parens" in {
    generate("def foo[A]: Either[A, Either[A, A]] <=> def bar[A]: Either[Either[A, A], A]") should be(
      iso("foo", "B", "bar", "B", "B")
    )
  }
}

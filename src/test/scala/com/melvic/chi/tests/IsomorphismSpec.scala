package com.melvic.chi.tests

import com.melvic.chi.config.Preferences
import com.melvic.chi.env.Env
import com.melvic.chi.eval.{Generate, generateAndShowCode, generateWith}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class IsomorphismSpec extends AnyFlatSpec with should.Matchers {
  def iso(leftName: String, leftArgs: String, rightName: String, rightArgs: String): String = {
    def argsString(args: String) = if (args.nonEmpty) s"[$args]" else ""
    val forall = if (leftArgs.nonEmpty) s", for all types $leftArgs" else ""
    s"$leftName${argsString(leftArgs)} Is isomorphic to $rightName${argsString(rightArgs)}$forall"
  }

  def isoNoTypeParams(leftName: String, rightName: String): String =
    iso(leftName, "", rightName, "")

  def notIso(leftName: String, rightName: String): String =
    s"$leftName is NOT isomorphic to $rightName"

  implicit val preferences: Preferences = Preferences.loadDefaults
  implicit val env: Env = Env.default

  def generate(signatureString: String) = generateAndShowCode(signatureString)

  "isomorphism" should "exist between two identities" in {
    generate("def foo[A]: A => A <=> def bar[B]: B => B") should be(
      iso("foo", "C", "bar", "C")
    )
  }

  it should "exist between alpha-equivalent functions" in {
    generate("def foo[A, B]: A => (A, B) <=> def bar[C, D]: C => (C, D)") should be(
      iso("foo", "F, E", "bar", "F, E")
    )
  }

  it should "consider the laws of exponents" in {
    generate("def foo[A, B]: A => (A, B) <=> def bar[A, B]: (B => B, B => A)") should be(
      iso("foo", "D, C", "bar", "C, D")
    )
  }

  it should "respect disjunction elimination" in {
    generate("def f[A, B, C]: (A => C, B => C) <=> def g[A, B, C]: Either[A, B] => C") should be(
      iso("f", "F, E, D", "g", "F, E, D")
    )
  }

  it should "respect conjunction associativity" in {
    generate("def foo[A]: (A, (A, A)) <=> def bar[B]: (B, B, B)") should be(
      iso("foo", "C", "bar", "C")
    )
    generate("def foo[A]: A => (A, (A, A)) <=> def bar[B]: B => (B, B, B)") should be(
      iso("foo", "C", "bar", "C")
    )
  }

  it should "exist between isomorphic functions with built-in types" in {
    generate("def foo: String => Int <=> def bar: String => Int") should be(
      isoNoTypeParams("foo", "bar")
    )
  }

  it should "respect disjunction associativity" in {
    generate("def fRight[A, B]: Either[A, Either[A, B]] <=> def fLeft[A, B]: Either[Either[A, A], B]") should be(
      iso("fRight", "D, C", "fLeft", "D, C")
    )
    generate("def foo[A]: A => Either[A, Either[A, A]] <=> def bar[B]: B => Either[Either[B, B], B]") should be(
      iso("foo", "C", "bar", "C")
    )
  }

  it should "support named parameters" in {
    generate("def len(str: String): Int <=> def toString(i: Int): String") should be(
      "len is NOT isomorphic to toString"
    )
    generate("def idA[A](value: A): A <=> def idB[A]: A => A") should be(
      iso("idA", "B", "idB", "B")
    )
    generate("def foo(a: String, b: Int): Float <=> def bar(b: Int, a: String): Float") should be(
      isoNoTypeParams("foo", "bar")
    )
  }
}

package com.melvic.chi.eval

import com.melvic.chi.ast.Proof
import com.melvic.chi.ast.Proof.{Abstraction, Application}
import com.melvic.chi.parsers.Language

object Simplifier {
  def simplyProof(proof: Proof, language: Language): Proof =
    language match {
      case Language.Scala => simplifyScala(proof)
      case Language.Java  => proof
    }

  def simplifyScala(proof: Proof): Proof =
    proof match {
      // e.g. `a => f(a)` becomes `f`
      case Abstraction(in, Application(function, param :: Nil)) if param == in =>
        simplifyScala(function)
      case Abstraction(in, out)          => Abstraction(simplifyScala(in), simplifyScala(out))
      case Application(function, params) => Application(simplifyScala(function), params.map(simplifyScala))
      case _                             => proof
    }
}

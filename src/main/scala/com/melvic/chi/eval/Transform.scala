package com.melvic.chi.eval

import com.melvic.chi.ast.Proof
import com.melvic.chi.ast.Proof.{Abstraction, Application, Variable}
import com.melvic.chi.ast.Proposition.PUnit
import com.melvic.chi.config.Preferences
import com.melvic.chi.config.PrefsContent.ScalaPrefs
import com.melvic.chi.parsers.Language

final case class Transform(proof: Proof) {
  def when(condition: => Boolean)(transform: Proof => Proof): Proof =
    if (condition) transform(proof) else proof
}

object Transform {
  def from(proof: Proof, language: Language)(implicit prefs: Preferences): Proof =
    language match {
      case Language.Scala => transformScala(proof)
      case Language.Java  => proof
    }

  def transformScala(proof: Proof)(implicit scalaPrefs: ScalaPrefs): Proof =
    proof match {
      // e.g. `a => f(a)` becomes `f`
      case Abstraction(in, Application(function, param :: Nil)) if param == in =>
        Transform(function).when(scalaPrefs.pointFree)(transformScala)
      case f @ Abstraction(in, out) if in == out =>
        Transform(f).when(scalaPrefs.usePredef)(_ => Variable("Predef.identity", PUnit))
      case function @ Abstraction(in, out) =>
        val result = Abstraction(transformScala(in), transformScala(out))
        if (result == function) result else transformScala(result)
      case Application(function, params) => Application(transformScala(function), params.map(transformScala))
      case _                             => proof
    }
}

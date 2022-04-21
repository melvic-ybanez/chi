package com.melvic.chi.eval

import com.melvic.chi.ast.Proof
import com.melvic.chi.ast.Proof.{Abstraction, Application, EitherCases, EitherMatch, Infix, Variable}
import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.parsers.Language

final case class Transform(proof: Proof) {
  def whenWith(condition: => Boolean)(transform: Proof => Proof): Proof =
    if (condition) transform(proof) else proof

  def when(condition: => Boolean)(as: => Proof): Proof =
    whenWith(condition)(_ => as)
}

object Transform {
  def from(
      proof: Proof,
      language: Language
  )(implicit prefs: Preferences, localFnName: String): Proof =
    language match {
      case Language.Scala => transformScala(proof)
      case _  => proof
    }

  def transformScala(proof: Proof)(implicit scalaPrefs: ScalaSettings, localFnName: String): Proof =
    proof match {
      // e.g. `a => f(a)` becomes `f`
      case Abstraction(in, Application(function, param :: Nil)) if param == in =>
        Transform(function).whenWith(scalaPrefs.pointFree)(transformScala)
      case f @ Abstraction(Variable(inName, _), EitherMatch(name, eitherCases)) if inName == name =>
        Transform(f).when(scalaPrefs.simplifyMatch)(transformScala(eitherCases))
      // e.g. `a => g(f(a))` becomes `g.compose(f)`
      case fg @ Abstraction(in, Application(f, Application(g, out :: Nil) :: Nil)) if in == out =>
        Transform(fg).when(scalaPrefs.pointFree) {
          Infix(f, Proof.applyOne(Proof.atomicVariable("compose"), g))
        }
      case f @ Abstraction(in, out) if in == out =>
        Transform(f).when(scalaPrefs.usePredef) {
          Proof.atomicVariable(resolveConflict("identity", "Predef"))
        }
      case function @ Abstraction(in, out) =>
        val result = Abstraction(transformScala(in), transformScala(out))

        // If after the components' transformations nothing has changed, stop the transformation.
        // Otherwise, transform the resulting function in case it can be further reduced.
        // For example `f => a => f(a)` reduces to `f => f`, which reduces to `identity`.
        if (result == function) result else transformScala(result)
      case Application(function, params) =>
        Application(transformScala(function), params.map(transformScala))
      case EitherMatch(name, eitherCases) =>
        transformScala(eitherCases) match {
          case e: EitherCases => EitherMatch(name, e)
          case proof => proof
        }
      case _ => proof
    }

  def resolveConflict(fnName: String, namespace: String)(implicit localFnName: String): String =
    if (fnName == localFnName) s"$namespace.$fnName"
    else fnName
}

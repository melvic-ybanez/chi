package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Abstraction, EitherCases, Variable}
import com.melvic.chi.ast.{Proof, Proposition}

object Utils {
  def showMatchUnion(name: Variable, either: EitherCases, showProof: Proof => String)(
      make: (Proposition, String, String) => String
  ) = {
    val newVars = name :: Nil
    val EitherCases(
      Abstraction(Variable(lName, lType), left),
      Abstraction(Variable(rName, _), right)
    ) = either

    val leftResult = showProof(Proof.rename(left, Variable.fromName(lName) :: Nil, newVars))
    val rightResult = showProof(Proof.rename(right, Variable.fromName(rName) :: Nil, newVars))

    make(lType, leftResult, rightResult)
  }
}

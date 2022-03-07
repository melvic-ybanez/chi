package com.melvic.chi

import com.melvic.chi.Fault.{CannotProve, ParseError}
import com.melvic.chi.ast.Proposition

object Result {
  type Result[A] = Either[Fault, A]

  def show(result: Result[String]): String =
    result match {
      case Left(CannotProve(proposition)) =>
        val propositionString = Proposition.show(proposition)
        s"Can not prove the following proposition: $propositionString"
      case Left(ParseError(msg)) => s"Parse Error: $msg"
      case Right(code) => code
    }

  def success[A](value: A): Result[A] = Right(value)

  def fail[A](fault: Fault): Result[A] = Left(fault)
}

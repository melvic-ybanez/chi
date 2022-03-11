package com.melvic.chi

import com.melvic.chi.ast.Definition

object Result {
  type Result[A] = Either[Fault, A]

  def show(result: Result[Definition]): String =
    result match {
      case Left(fault) => Fault.show(fault)
      case Right(code) => Definition.show(code)
    }

  def success[A](value: A): Result[A] = Right(value)

  def fail[A](fault: Fault): Result[A] = Left(fault)
}

package com.melvic.chi.output

sealed trait IsoResult

object IsoResult {
  final case class Success(left: (String, List[String]), right: (String, List[String])) extends IsoResult
  final case class Fail(leftName: String, rightName: String) extends IsoResult
}

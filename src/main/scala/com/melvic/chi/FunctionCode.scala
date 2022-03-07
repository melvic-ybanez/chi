package com.melvic.chi

import com.melvic.chi.ast.Proposition

final case class FunctionCode(name: String, typeParams: List[String], proposition: Proposition)

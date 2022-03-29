package com.melvic.chi.ast

import com.melvic.chi.ast.Proof.Variable

final case class Signature(
    name: String,
    typeParams: List[String],
    params: List[Variable],
    proposition: Proposition
)

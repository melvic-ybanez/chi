package com.melvic.chi.ast

import com.melvic.chi.parsers.Language

final case class Definition(signature: Signature, body: Proof, language: Language)

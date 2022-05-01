package com.melvic.chi

import com.melvic.chi.ast.{Definition, Proof, Signature}

package object output {
  type Layout[A] = A => String
  type DefLayout = Layout[Definition]
  type SignatureLayout = Layout[Signature]
  type ProofLayout = Layout[Proof]
}

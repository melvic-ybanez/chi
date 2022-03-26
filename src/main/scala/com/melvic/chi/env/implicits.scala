package com.melvic.chi.env

import com.melvic.chi.ast.Proof
import com.melvic.chi.env.Environment.Environment

object implicits {
  implicit class EnvironmentOps(env: Environment) {
    def discharge(proof: Proof): Environment =
      Environment.discharge(env, proof)
  }
}

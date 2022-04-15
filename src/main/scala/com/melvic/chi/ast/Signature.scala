package com.melvic.chi.ast

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition.{Atom, Identifier, PUnit}
import com.melvic.chi.env.Env

final case class Signature(
    name: String,
    typeParams: List[String],
    params: List[Variable],
    returnType: Proposition
)

object Signature {
  def isomorphic(signature: Signature, signature1: Signature): Boolean = {
    val proposition = Proposition.normalize(Proposition.fromSignature(signature))
    val proposition1 = Proposition.normalize(Proposition.fromSignature(signature1))

    // Fetch the used atoms. Do not rely on the type parameters as not
    // all of them will be used in the proposition. It is important to rename only
    // the used ones.
    val atoms = Proposition.atoms(proposition)
    val atoms1 = Proposition.atoms(proposition1)

    val typeArgs = {
      val atomsCombined = atoms ++ atoms1
      val env = Env.fromList(atomsCombined.map(atom => Variable(atom.value, atom)))
      val (renamedAtoms, _) = atomsCombined.foldLeft((List.empty[Atom], Env.default)) {
        case ((acc, env), atom) =>
          val atomName = Env.generateName(atom.value)(env)
          (Identifier(atomName) :: acc, Env.addProof(Variable(atomName, PUnit))(env))
      }
      renamedAtoms
    }

    val renamedProposition = Proposition.rename(proposition, atoms, typeArgs)
    val typeArgsPerm = typeArgs.permutations
    typeArgsPerm.exists { typeArgs =>
      val renamedProposition1 = Proposition.rename(proposition1, atoms1, typeArgs)
      Proposition.isomorphic(renamedProposition, renamedProposition1)
    }
  }
}

package com.melvic.chi.ast

import com.melvic.chi.ast.Proof.Variable
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.env.Env
import com.melvic.chi.output.IsoResult

final case class Signature(
    name: String,
    typeParams: List[String],
    params: List[Variable],
    returnType: Proposition
)

object Signature {

  /**
   * Constructs a proposition with the named parameters as the conjunction antecedent and the return type as
   * the consequent
   */
  def fullProposition(signature: Signature): Proposition = {
    val Signature(_, _, params, returnType) = signature
    Implication(Conjunction(params.map(_.proposition)), returnType)
  }

  def isomorphic(signature: Signature, signature1: Signature): IsoResult = {
    val proposition = Proposition.normalize(Proposition.fromSignature(signature))
    val proposition1 = Proposition.normalize(Proposition.fromSignature(signature1))

    def usedTypeParam(signature: Signature, proposition: Proposition): List[Atom] =
      signature.typeParams.map(Identifier).filter(atom => Proposition.exists(proposition)(_ == atom))

    val atoms = usedTypeParam(signature, proposition)
    val atoms1 = usedTypeParam(signature1, proposition1)

    // Find a set of type arguments whose names do not conflict with the names of the
    // type parameters from both propositions. This is needed to allow isomorphic
    // types that happen to use different type parameter names.
    val typeArgs = {
      val env = Env.fromList((atoms ++ atoms1).map(atom => Variable(atom.value, atom)))
      val min = math.min(atoms.length, atoms.length)
      val (renamedAtoms, _) = (0 until min).foldLeft((List.empty[Atom], env)) { case ((acc, env), atom) =>
        val atomName = Env.generateName("A")(env)
        (Identifier(atomName) :: acc, Env.addProof(Variable(atomName, PUnit))(env))
      }
      renamedAtoms
    }

    val renamedProposition = Proposition.rename(proposition, atoms, typeArgs)
    val typeArgsPerm = typeArgs.permutations
    val correctTypeArgs = typeArgsPerm.find { typeArgs =>
      val renamedProposition1 = Proposition.rename(proposition1, atoms1, typeArgs)
      Proposition.isomorphic(renamedProposition, renamedProposition1)
    }

    correctTypeArgs
      .map { typeArgs1 =>
        IsoResult.Success(
          (signature.name, typeArgs.map(_.value)),
          (signature1.name, typeArgs1.map(_.value))
        )
      }
      .getOrElse(IsoResult.Fail(signature.name, signature1.name))
  }
}

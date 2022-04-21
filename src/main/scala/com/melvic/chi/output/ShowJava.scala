package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{Abstraction, Application, Variable}
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Implication}
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowJava extends Display {
  override def showSignature(signature: Signature, split: Boolean) = {
    val Signature(name, typeParams, params, proposition) = signature
    val typeParamString = typeParams match {
      case Nil => ""
      case _ => s"<${Utils.toCSV(typeParams)}> "
    }
    val paramsString = {
      val vars = params.map {
        case Variable(name, proposition) =>
          s"${showProposition(proposition)} $name"
      }
      Utils.splitParams(vars, split)
    }

    s"$typeParamString${showProposition(proposition)} $name($paramsString)"
  }

  override def showProposition(proposition: Proposition) =
    proposition match {
      case Atom(value) => value
      case Implication(Conjunction(a :: b :: Nil), consequent) =>
        s"BiFunction<${showProposition(a)}, ${showProposition(b)}, ${showProposition(consequent)}>"
      case Implication(antecedent, consequent) =>
        s"Function<${showProposition(antecedent)}, ${showProposition(consequent)}>"
      case _ => ""
    }

  override def showProofWithLevel(proof: Proof, level: Option[Int]) = {
    val indent = "    "
    val bodyIndent = indent * 2
    val proofString = proof match {
      case Variable(name, _) => name
      case Abstraction(Proof.Conjunction(a :: b :: Nil), antecedent) =>
        val outString = showProof(antecedent)
        s"(${showProof(a)}, ${showProof(b)}) -> {\n${bodyIndent}return $outString;\n$indent}"
      case Abstraction(in, out) =>
        s"${showProof(in)} -> ${showProof(out)}"
      case Application(function, params) =>
        val functionString = showProof(function)
        s"$functionString.apply(${Utils.toCSV(params.map(showProof))})"
      case _ => ""
    }
    s"$proofString"
  }

  override def showDefinition(signature: String, body: String, pretty: Boolean) =
    s"$signature {\n    return $body;\n}"

  override def numberOfSpacesForIndent = 4
}

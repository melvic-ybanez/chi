package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{Abstraction, Application, Variable}
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Implication}
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowJava extends Show { show =>
  override def signature(signature: Signature, split: Boolean) = {
    val Signature(name, typeParams, params, proposition) = signature
    val typeParamString = typeParams match {
      case Nil => ""
      case _   => s"<${Show.toCSV(typeParams)}> "
    }
    val paramsString = {
      val vars = params.map {
        case Variable(name, proposition) =>
          s"${show.proposition(proposition)} $name"
      }
      Show.splitParams(vars, split)
    }

    s"$typeParamString${show.proposition(proposition)} $name($paramsString)"
  }

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value) => value
      case Implication(Conjunction(a :: b :: Nil), consequent) =>
        s"BiFunction<${show.proposition(a)}, ${show.proposition(b)}, ${show.proposition(consequent)}>"
      case Implication(antecedent, consequent) =>
        s"Function<${show.proposition(antecedent)}, ${show.proposition(consequent)}>"
      case _ => ""
    }

  override def proofWithLevel(proof: Proof, level: Option[Int]) = {
    val indent = "    "
    val bodyIndent = indent * 2
    val proofString = proof match {
      case Variable(name, _) => name
      case Abstraction(Proof.Conjunction(a :: b :: Nil), antecedent) =>
        val outString = show.proof(antecedent)
        s"(${show.proof(a)}, ${show.proof(b)}) -> {\n${bodyIndent}return $outString;\n$indent}"
      case Abstraction(in, out) =>
        s"${show.proof(in)} -> ${show.proof(out)}"
      case Application(function, params) =>
        val functionString = show.proof(function)
        s"$functionString.apply(${Show.toCSV(params.map(show.proof))})"
      case _ => ""
    }
    s"$proofString"
  }

  override def definition(signature: String, body: String, pretty: Boolean) =
    s"$signature {\n${singleIndent}return $body;\n}"

  override def indentWidth = 4
}

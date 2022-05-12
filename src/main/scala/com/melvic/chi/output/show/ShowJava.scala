package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Abstraction, Application, Variable}
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Implication}
import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.{ProofLayout, SignatureLayout}

class ShowJava(implicit val prefs: Preferences) extends Show with CLike { show =>
  override def signature = signatureWithSplit(false)

  override def prettySignature = signatureWithSplit(true)

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, typeParams, params, proposition) =>
      val typeParamString = typeParams match {
        case Nil => ""
        case _   => s"<${Show.toCSV(typeParams)}> "
      }
      val paramsString = {
        val vars = params.map {
          case Variable(name, proposition) =>
            s"${show.proposition(proposition)} $name"
        }
        Show.splitParams(vars, split, indentWidth)
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

  def proof: ProofLayout = {
    case Variable(name, _) => name
    case Abstraction(Proof.Conjunction(a :: b :: Nil), antecedent) =>
      val outString = show.proof(antecedent)
      val bodyString = nest(line + "return " + outString)
      s"(${show.proof(a)}, ${show.proof(b)}) -> {$bodyString;$line}"
    case Abstraction(in, out) =>
      s"${show.proof(in)} -> ${show.proof(out)}"
    case Application(function, params) =>
      val functionString = show.proof(function)
      s"$functionString.apply(${Show.toCSV(params.map(show.proof))})"
    case _ => ""
  }
}

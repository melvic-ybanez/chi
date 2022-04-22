package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Disjunction, Implication}
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowHaskell(functionName: String) extends Display {
  override def showSignature(signature: Signature, split: Boolean) =
    s"${signature.name} :: ${showProposition(signature.returnType)}"

  override def showProposition(proposition: Proposition) =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + Utils.toCSV(components.map(showProposition)) + ")"
      case Disjunction(left, right)            => s"Either ${showProposition(left)} ${showProposition(right)}"
      case Implication(impl: Implication, out) => s"(${showProposition(impl)}) -> ${showProposition(out)}"
      case Implication(in, out)                => s"${showProposition(in)} -> ${showProposition(out)}"
    }

  override def showProofWithLevel(proof: Proof, level: Option[Int]) = {
    val proofString = showBodyProofWithLevel(proof, level)
    val sep = proof match {
      case TUnit => " = "
      case _     => " "
    }
    s"$functionName$sep$proofString"
  }

  /**
    * Like [[showProofWithLevel]], but without the function name
    */
  def showBodyProofWithLevel(proof: Proof, level: Option[Int]): String = {
    val indent = this.indent(level.map(_ + 1).orElse(Some(1)))

    def showLambda: Abstraction => String = {
      case Abstraction(in, out) => s"\\${showBodyProof(in)} -> ${showBodyProof(out)}"
    }

    proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case PConjunction(components) =>
        def showParam: Proof => String = {
          case abs: Abstraction => showLambda(abs)
          case proof            => showBodyProof(proof)
        }
        s"(${Utils.toCSV(components.map(showParam))})"
      case PRight(proof) => s"Right ${showBodyProof(proof)}"
      case PLeft(proof)  => s"Left ${showBodyProof(proof)}"
      case Match(name, EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut))) =>
        val leftCase = s"Left ${showBodyProof(leftIn)} -> ${showBodyProof(leftOut)}"
        val rightCase = s"Right ${showBodyProof(rightIn)} -> ${showBodyProof(rightOut)}"
        s"case $name of\n$indent$leftCase\n$indent$rightCase"
      case Abstraction(in, out: Abstraction) => s"${showBodyProof(in)} ${showBodyProof(out)}"
      case Abstraction(in, out)              => s"${showBodyProof(in)} = ${showBodyProof(out)}"
      case Application(function, params) =>
        def showParam(param: Proof): String =
          param match {
            case abs: Abstraction => s"(${showLambda(abs)})"
            case app: Application => s"(${showBodyProof(app)})"
            case left: PLeft      => s"(${showBodyProof(left)})"
            case right: PRight    => s"(${showBodyProof(right)})"
            case _                => showBodyProof(param)
          }
        s"${showBodyProof(function)} ${params.map(showParam).mkString(" ")}"
      // For now, we can only retrieve the 1st and 2nd element of a tuple
      case Indexed(proof, index) =>
        val function = if (index == 1) "fst" else "snd"
        showBodyProofWithLevel(Proof.applyOne(Proof.atomicVariable(function), proof), level)
    }
  }

  def showBodyProof(proof: Proof): String = showBodyProofWithLevel(proof, None)

  override def showDefinition(signature: String, body: String, pretty: Boolean) =
    s"$signature\n$body"

  override def numberOfSpacesForIndent = 4
}

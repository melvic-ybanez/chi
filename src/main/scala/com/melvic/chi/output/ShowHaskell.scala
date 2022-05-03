package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Disjunction, Implication}
import com.melvic.chi.ast.{Proof, Proposition}
import com.melvic.chi.config.Preferences

class ShowHaskell(functionName: String)(implicit val prefs: Preferences) extends Show { show =>
  override def signature: SignatureLayout =
    signature => s"${signature.name} :: ${show.proposition(signature.returnType)}"

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)                         => value
      case Conjunction(components)             => "(" + Show.toCSV(components.map(show.proposition)) + ")"
      case Disjunction(left, right)            => s"Either ${show.proposition(left)} ${show.proposition(right)}"
      case Implication(impl: Implication, out) => s"(${show.proposition(impl)}) -> ${show.proposition(out)}"
      case Implication(in, out)                => s"${show.proposition(in)} -> ${show.proposition(out)}"
    }

  def proof: ProofLayout = { proof =>
    val proofString = namelessBody(proof)
    val sep = proof match {
      case TUnit => " = "
      case _     => " "
    }
    s"$functionName$sep$proofString"
  }

  override def bodyLayouts: List[ProofLayout] = proof :: Nil

  /**
    * Like [[show.proof]], but without the function name
    */
  def namelessBody(proof: Proof): String = {
    def showLambda: Abstraction => String = {
      case Abstraction(in, out) => s"\\${namelessBody(in)} -> ${namelessBody(out)}"
    }

    proof match {
      case TUnit             => "()"
      case Variable(name, _) => name
      case PConjunction(components) =>
        def showParam: Proof => String = {
          case abs: Abstraction => showLambda(abs)
          case proof            => namelessBody(proof)
        }
        s"(${Show.toCSV(components.map(showParam))})"
      case PRight(proof) => s"Right ${namelessBody(proof)}"
      case PLeft(proof)  => s"Left ${namelessBody(proof)}"
      case EitherCases(Abstraction(leftIn, leftOut), Abstraction(rightIn, rightOut)) =>
        val leftCase = s"Left ${namelessBody(leftIn)} -> ${namelessBody(leftOut)}"
        val rightCase = s"Right ${namelessBody(rightIn)} -> ${namelessBody(rightOut)}"
        s"$leftCase$line$rightCase"
      case Match(name, term: Proof) =>
        val cases = nest(line + namelessBody(term))
        s"case $name of$cases"
      case Abstraction(in, out: Abstraction) => s"${namelessBody(in)} ${namelessBody(out)}"
      case Abstraction(in, out)              => s"${namelessBody(in)} = ${namelessBody(out)}"
      case Application(function, params) =>
        def showParam(param: Proof): String =
          param match {
            case abs: Abstraction => s"(${showLambda(abs)})"
            case app: Application => s"(${namelessBody(app)})"
            case left: PLeft      => s"(${namelessBody(left)})"
            case right: PRight    => s"(${namelessBody(right)})"
            case _                => namelessBody(param)
          }
        s"${namelessBody(function)} ${params.map(showParam).mkString(" ")}"
      // For now, we can only retrieve the 1st and 2nd element of a tuple
      case Indexed(proof, index) =>
        val function = if (index == 1) "fst" else "snd"
        namelessBody(Application.oneArg(Variable.fromName(function), proof))
    }
  }

  override def prettySignature = signature

  override def indentWidth = 2

  override def makeDef(signature: String, body: String) =
    signature + line + body
}

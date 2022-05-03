package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{EitherCases, Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}
import com.melvic.chi.config.Preferences

class ShowPython(implicit val prefs: Preferences) extends Show { show =>
  override def bodyLayouts = proof :: Nil

  override def signature = signatureWithSplit(false)

  override def prettySignature = signatureWithSplit(true)

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)              => value
      case Conjunction(components)  => "Tuple[" + show.propositionCSV(components) + "]"
      case disjunction: Disjunction =>
        // unions in python are always flattened
        val Union(components) = Union.fromDisjunction(disjunction)
        s"Union[${show.propositionCSV(components)}]"
      case Implication(Conjunction(components), consequent) =>
        s"Callable[[${propositionCSV(components)}], ${show.proposition(consequent)}]"
    }

  def proof: ProofLayout = {
    case Variable(name, _)        => name
    case PConjunction(components) => s"(${bodyCSV(components)})"
    case PLeft(proof)             => show.proof(proof)
    case PRight(proof)            => show.proof(proof)
    case Match(name, e @ EitherCases(Abstraction(_: Variable, _), Abstraction(_: Variable, _))) =>
      val newVars = Variable.fromName(name) :: Nil
      val EitherCases(
        Abstraction(Variable(lName, lType), left),
        Abstraction(Variable(rName, rType), right)
      ) = e

      val leftResult = Proof.rename(left, Variable.fromName(lName) :: Nil, newVars)
      val rigthResult = Proof.rename(right, Variable.fromName(rName) :: Nil, newVars)

      s"$leftResult if isinstance($name, $lType) else $rigthResult"
    case Match(name, proof: Proof)                  => show.proof(proof)
    case Abstraction(PConjunction(Nil), out)        => s"lambda: ${show.proof(out)}"
    case Abstraction(PConjunction(components), out) => s"lambda ${bodyCSV(components)}: ${show.proof(out)}"
    case Application(function, params) =>
      s"${show.proof(function)}(${bodyCSV(params)})"
    case Infix(left, right) =>
      s"${show.proof(left)}.${show.proof(right)}"
    case Indexed(proof, index) => s"${show.proof(proof)}[${index - 1}]"
  }

  def bodyCSV(components: List[Proof]): String =
    csv(components)(show.proof)

  override def indentWidth = 4

  override def makeDef(signature: String, body: String) =
    signature + nest(line + "return " + body)

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, _, params, returnType) =>
      val paramsString = paramsList(params, split)

      s"def $name$paramsString -> ${show.proposition(returnType)}:"
  }
}

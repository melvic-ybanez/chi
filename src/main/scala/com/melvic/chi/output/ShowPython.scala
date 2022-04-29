package com.melvic.chi.output
import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proof, Proposition, Signature}

class ShowPython extends Show { show =>
  override def signature(signature: Signature, split: Boolean) = {
    val Signature(name, _, params, returnType) = signature
    s"def $name(${paramsList(params, split)}) -> $returnType"
  }

  override def proposition(proposition: Proposition) = {
    def showComponents(components: List[Proposition]): String =
      Show.toCSV(components.map(show.proposition))

    proposition match {
      case Atom(value)              => value
      case Conjunction(components)  => s"Tuple[${showComponents(components)}]"
      case disjunction: Disjunction =>
        // unions in python are flattened
        val Union(components) = Union.fromDisjunction(disjunction)

        s"Union[${showComponents(components)}]"
      case Implication(PUnit, consequent) => s"Callable[[], ${this.proposition(consequent)}]"
      case Implication(Conjunction(components), consequent) =>
        s"Callable[[${showComponents(components)}], ${this.proposition(consequent)}]"
    }
  }

  override def proofWithLevel(proof: Proof, level: Option[Int]) =
    proof match {
      case Variable(name, _) => name
      case PConjunction(components) => "(" + Show.toCSV(components.map(show.proof)) + ")"
      case PLeft(proof) => show.proof(proof)
      case PRight(proof) => show.proof(proof)
      case Match(name, EitherCases(left, right)) =>
        val Union(components) = Union.fromDisjunction(Disjunction(left, right))
      case Application(function, params) =>
        val argsString = params.map(show.proof)
        s"${show.proof(function)}($argsString)"
    }

  override def definition(signature: String, body: String, pretty: Boolean) =
    s"$signature {\n${singleIndent}return $body;\n}"

  override def indentWidth = 4
}

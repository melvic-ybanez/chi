package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.{ParamsInParens, ProofLayout, SignatureLayout}

class ShowTypescript(implicit val prefs: Preferences)
    extends Show
    with ScalaLike
    with ParamsInParens
    with CLike { show =>
  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value)              => value
      case Conjunction(components)  => "[" + propositionCSV(components) + "]"
      case disjunction: Disjunction =>
        // unions in Chi are always flattened
        val Union(components) = Union.fromDisjunction(disjunction)
        show.propositionCSV(components, " | ")
      // Implications in Chi only has conjunction as the type of antecedent
      case Implication(Labeled(paramName, paramType), consequent) =>
        s"($paramName: ${show.proposition(paramType)}) => ${show.proposition(consequent)}"
      case Implication(Conjunction(components), consequent) =>
        val componentString = Show.toCSV(components.map { case Labeled(paramName, paramType) =>
          paramName + ": " + show.proposition(paramType)
        })
        s"($componentString) => ${show.proposition(consequent)}"
    }

  def proof: ProofLayout = {
    case Variable(name, _)        => name
    case PConjunction(components) => "[" + csv(components)(show.proof) + "]"
    case PLeft(proof)             => show.proof(proof)
    case PRight(proof)            => show.proof(proof)
    case Match(name, ec @ EitherCases(Abstraction(_: Variable, _), Abstraction(_: Variable, _))) =>
      Utils.showMatchUnion(name, ec, show.proof) { (lType, leftResult, rType, rightResult) =>
        def condition(code: String, componentType: Proposition) =
          nest(s"if (typeof($name) == '$componentType') ${line}return $code") + line
        val leftCondition = condition(leftResult, lType)
        val rightCondition = "else " + condition(rightResult, rType)
        val blockContent = leftCondition + line + rightCondition
        val block = nest(s"{$line$blockContent") + line + "}"
        s"(() => $block)()"
      }
    case Match(name, Abstraction(PConjunction(components), body)) =>
      val destructure = s"const [${csv(components)(show.proof)}] = $name"
      val blockContent = "return " + destructure + line + show.proof(body)
      val block = nest(s"{$line$blockContent") + line + "}"
      s"(() => $block)()"
    case Abstraction(Variable(name, paramType), out) =>
      s"($name: ${show.proposition(paramType)}) => ${show.proof(out)}"
    case Abstraction(PConjunction(components), out) =>
      val componentString = Show.toCSV(components.map { case variable: Variable =>
        Labeled.fromVariable(variable)
      })
      s"($componentString) => ${show.proof(out)}"
    case Application(function: Abstraction, params) =>
      s"(${show.proof(function)})(${csv(params)(show.proof)})"
    case Application(function, params) =>
      s"${show.proof(function)}(${csv(params)(show.proof)})"
    case Infix(left, right) =>
      s"${show.proof(left)}.${show.proof(right)}"
    case Indexed(proof, index) => s"${show.proof(proof)}[${index - 1}]"
  }

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, typeParams, params, returnType) =>
      val typeParamsString = typeParams match {
        case Nil        => ""
        case typeParams => s"<${Show.toCSV(typeParams)}>"
      }
      val paramsString = paramList(params, split)

      s"function $name$typeParamsString$paramsString: ${show.proposition(returnType)} "
  }
}

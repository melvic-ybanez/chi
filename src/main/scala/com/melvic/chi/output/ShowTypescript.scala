package com.melvic.chi.output

import com.melvic.chi.ast.Proposition.{Atom, Conjunction, Disjunction, Implication, Union}
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.config.Preferences

class ShowTypescript(implicit val prefs: Preferences) extends Show with ScalaLike with ParamsInParens {
  show =>

  override def bodyLayouts = ???

  override def proposition(proposition: Proposition) =
    proposition match {
      case Atom(value) => value
      case Conjunction(components) => "[" + propositionCSV(components) + "]"
      case disjunction: Disjunction =>
        // unions in Chi are always flattened
        val Union(components) = Union.fromDisjunction(disjunction)
        show.propositionCSV(components, " | ")
        // Implications in Chi only has conjunction as the type of antecedent
      case Implication(Conjunction(atom :: paramType :: Nil), consequent) =>
        s"(${show.proposition(atom)}: ${show.proposition(paramType)}) => ${show.proposition(consequent)}"
      case Implication(Conjunction(components), consequent) =>
        val componentString = Show.toCSV(components.map {
          case Conjunction(atom :: paramType :: Nil) => show.proposition(atom) + ": " + show.proposition(paramType)
        })
        s"($componentString) => ${show.proposition(consequent)}"
    }

  override def indentWidth = 4

  override def makeDef(signature: String, body: String) =
    signature + " " + body

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, typeParams, params, returnType) =>
      val typeParamsString = typeParams match {
        case Nil        => ""
        case typeParams => s"<${Show.toCSV(typeParams)}>"
      }
      val paramsString = paramList(params, split)

      s"function $name$typeParamsString$paramsString: ${show.proposition(returnType)} ="
  }
}

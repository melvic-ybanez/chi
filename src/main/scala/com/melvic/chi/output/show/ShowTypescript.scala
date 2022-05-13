package com.melvic.chi.output.show

import com.melvic.chi.ast.Proof.{Conjunction => PConjunction, _}
import com.melvic.chi.ast.Proposition._
import com.melvic.chi.ast.{Proposition, Signature}
import com.melvic.chi.config.Preferences
import com.melvic.chi.output.{ParamsInParens, ProofLayout, SignatureLayout}
import com.melvic.chi.parsers.Language

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
    case Match(name, EitherCases(left @ Abstraction(Variable(_, lType), _), right)) =>
      val ifBranch = {
        val ifBody = show.proof(Show.useUnionNameInBranch(left, name))
        val predicate = s"typeof(${show.proof(name)}) === \"${show.proposition(lType)}\""
        val condition = lType match {
          case Atom(typeName) =>
            if (Language.Typescript.builtInTypes.contains(typeName)) predicate
            else nest(Show.error(s"Typescript support does not$line include unions of non-builtin types"))
          case _ => predicate
        }
        nest(s"if ($condition)${line}return $ifBody")
      }
      val elseBranch = "else return " + show.proof(Show.useUnionNameInBranch(right, name))
      show.ifElse(ifBranch, elseBranch)
    case Match(name, EitherCases(Abstraction(PConjunction(lInComps), lOut), right)) =>
      val ifBranch = {
        val nameString = show.proof(name)
        val destructure = s"const [${csv(lInComps)(show.proof)}] = $nameString"
        val condition = {
          val warning = nest(
            Show.warning("You might need to do" + line + "extra checks for the types of the components")
          )
          s"if ($nameString instanceof Array $warning)"
        }
        val ifBody = show.proof(lOut)
        nest(condition + " {" + line + destructure + line + "return " + ifBody)
      }
      val elseBranch = "} else return " + show.proof(Show.useUnionNameInBranch(right, name))
      show.ifElse(ifBranch, elseBranch)
    case Match(name, Abstraction(PConjunction(components), body)) =>
      val destructure = s"const [${csv(components)(show.proof)}] = ${show.proof(name)}"
      val comment = "// Note: This is verbose for compatibility reasons"
      val blockContent = comment + line + destructure + line + "return " + show.proof(body)
      show.invokedLambda(blockContent)
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
    case _                     => Show.error()
  }

  def signatureWithSplit(split: Boolean): SignatureLayout = {
    case Signature(name, typeParams, params, returnType) =>
      val typeParamsString = typeParams match {
        case Nil        => ""
        case typeParams => s"<${Show.toCSV(typeParams)}>"
      }
      val paramsString = paramList(params, split)

      s"function $name$typeParamsString$paramsString: ${show.proposition(returnType)}"
  }

  private def ifElse(ifBranch: String, elseBranch: String): String =
    invokedLambda(ifBranch + line + elseBranch)

  private def invokedLambda(blockContent: String): String = {
    val block = nest(s"{$line$blockContent") + line + "}"
    s"(() => $block)()"
  }
}

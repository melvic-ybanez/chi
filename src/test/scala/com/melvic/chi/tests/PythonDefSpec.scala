package com.melvic.chi.tests

import com.melvic.chi.eval.{Generate, generateAndShowWithInfo}
import com.melvic.chi.parsers.Language

class PythonDefSpec extends BaseSpec {
  override val language = Language.Python

  def input(typeVars: List[String], input: String): String =
    Generate.allToString(typeVars.map(t => s"$t = TypeVar('$t')") ++ List(input))

  "identity" should "map the input to itself" in {
    input("A" :: Nil, "def id(a: A) -> A") should be(
      output(
        """def id(a: A) -> A:
          |    return a""".stripMargin
      )
    )
  }

  "fst" should "return the first element" in {
    input("A" :: "B" :: Nil, "def fst(tup: Tuple[A, B]) -> A") should be(
      output(
        """def fst(tup: Tuple[A, B]) -> A:
          |    return (lambda a, b: a)(*tup)""".stripMargin
      )
    )
  }

  "compose" should "apply the first function after the second" in {
    input(
      "A" :: "B" :: "C" :: Nil,
      "def compose(f: Callable[[B], C], g: Callable[[A], B]) -> Callable[[A], C]"
    ) should be(
      output(
        """def compose(f: Callable[[B], C], g: Callable[[A], B]) -> Callable[[A], C]:
          |    return lambda a: f(g(a))""".stripMargin
      )
    )
  }

  "conjunction" should "depend on the proofs of its components" in {
    input("A" :: "B" :: "C" :: Nil, "def foo(a: A, f: Callable[[B], Tuple[A, B]], b: B) -> Tuple[A, B]") should be(
      output(
        """def foo(a: A, f: Callable[[B], Tuple[A, B]], b: B) -> Tuple[A, B]:
          |    return (a, b)""".stripMargin
      )
    )
    generateAndShowWithInfo("def foo(t: Tuple[Callable[[int], str], float], i: int) -> str") should be(
      output(
        """def foo(t: Tuple[Callable[[int], str], float], i: int) -> str:
          |    return (lambda f, g: f(i))(*t)""".stripMargin
      )
    )
  }

  "either" should "default to left when the evaluation succeeds" in {
    input("A" :: "B" :: "C" :: Nil, "def left(a: A) -> Union[A, B]") should be(
      output(
        """def left(a: A) -> Union[A, B]:
          |    return a""".stripMargin
      )
    )
  }

  "All assumptions" should "be considered" in {
    input("A" :: "B" :: "C" :: Nil, "def foo(f: Callable[[A], C], g: Callable[[B], C], b: B) -> C") should be(
      output(
        """def foo(f: Callable[[A], C], g: Callable[[B], C], b: B) -> C:
          |    return g(b)""".stripMargin
      )
    )
    input("A" :: "B" :: "C" :: Nil, "def foo(f: Callable[[B], C], g: Callable[[A], C], b: B) -> C") should be(
      output(
        """def foo(f: Callable[[B], C], g: Callable[[A], C], b: B) -> C:
          |    return f(b)""".stripMargin
      )
    )
  }

  "implication" should "evaluate it's antecedent recursive" in {
    input("A" :: "B" :: "C" :: Nil, "def foo(f: Callable[[A], B], g: Callable[[Callable[[A], B]], C]) -> C") should be(
      output(
        """def foo(f: Callable[[A], B], g: Callable[[Callable[[A], B]], C]) -> C:
          |    return g(lambda a: f(a))""".stripMargin
      )
    )
  }

  "disjunction elimination" should "work as formalized in propositional logic" in {
    input(
      "A" :: "B" :: "C" :: Nil,
      "def de(f: Callable[[A], C], g: Callable[[B], C]) -> Callable[[Union[A, B]], C]"
    ) should be(
      output(
        """def de(f: Callable[[A], C], g: Callable[[B], C]) -> Callable[[Union[A, B]], C]:
          |    return lambda e: f(e) if type(e) is A else g(e)""".stripMargin
      )
    )
  }

  "Built-in types" should "be included in the type search" in {
    generateAndShowWithInfo(
      """def de(
        |	f: Callable[[int], float],
        |	g: Callable[[str], float]
        |) -> Callable[[Union[int, str]], float]""".stripMargin
    ) should be(
      output(
        """def de(
          |    f: Callable[[int], float],
          |    g: Callable[[str], float]
          |) -> Callable[[Union[int, str]], float]:
          |    return lambda e: f(e) if type(e) is int else g(e)""".stripMargin
      )
    )
  }

  "component of a product consequent" should "be accessible if the function is applied" in {
    input("A" :: "B" :: "C" :: Nil, "def foo(f: Callable[[A], Tuple[C, B]], a: A) -> C") should be(
      output(
        """def foo(f: Callable[[A], Tuple[C, B]], a: A) -> C:
          |    return f(a)[0]""".stripMargin
      )
    )
  }
}

# Chi
A code generator based on the Curry-Howard Isomorphism

Chi takes in a function signature as a string and generates the function implementation.

The following examples illustrate how to get an output string using the convenience function
`generateAndShow`:

```scala
scala> generateAndShow("def identity[A]: A => A")
val res0: String =
def identity[A]: A => A =
  a => a

scala> generateAndShow("def apply[A, B]: (A => B) => A => B")
val res1: String =
def apply[A, B]: A => B => A => B =
  f => a => f(a)

scala> generateAndShow("def fst[A, B]: (A, B) => A")
val res2: String =
def fst[A, B]: (A, B) => A =
  { case (a, b) =>
    a
  }

scala> generateAndShow("def const[A, B]: A => (B => A)")
val res3: String =
def const[A, B]: A => B => A =
  a => b => a

scala> generateAndShow("def compose[A, B, C]: (B => C) => (A => B) => (A => C)")
val res4: String =
def compose[A, B, C]: B => C => A => B => A => C =
  f => g => a => f(g(a))

scala> generateAndShow("def andThen[A, B, C]: (A => B) => (B => C) => (A => C)")
val res5: String =
def andThen[A, B, C]: A => B => B => C => A => C =
  f => g => a => g(f(a))

```
# Chi
A code generator for universally quantified function signatures. Chi stands 
for [Curry-Howard Isomorphism](https://en.wikipedia.org/wiki/Curry%E2%80%93Howard_correspondence). 
This program is currently just a proof-of-concept to see how mechanical functions really
are when their types serve as logical propositions under the Curry-Howard Isomorphism.

Chi takes in a function signature as a string and generates the function implementation.

The following examples illustrate how to get an output string using the convenience function
`generateAndShow`:

```scala
scala> generateAndShow("def identity[A]: A => A")
val res0: String =
def identity[A]: A => A =
  a => a

scala> generateAndShow("def apply[A, B]: (A => B) => A => B")
val res3: String =
def apply[A, B]: ((A => B) => (A => B)) =
  f => a => f(a)

scala> generateAndShow("def fst[A, B]: (A, B) => A")
val res4: String =
def fst[A, B]: ((A, B) => A) =
{ case (a, b) =>
  a
}

scala> generateAndShow("def const[A, B]: A => (B => A)")
val res5: String =
def const[A, B]: (A => (B => A)) =
  a => b => a

scala> generateAndShow("def compose[A, B, C]: (B => C) => (A => B) => A => C")
val res6: String =
def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
  f => g => a => f(g(a))

scala> generateAndShow("def andThen[A, B, C]: (A => B) => (B => C) => A => C")
val res7: String =
def andThen[A, B, C]: ((A => B) => ((B => C) => (A => C))) =
  f => g => a => g(f(a))
```

Named parameters are also supported:

```scala
import com.melvic.chi._

scala> generateAndShow("def identity(a: A): A")
val res0: String =
def identity(a: A): A =
  a

scala> generateAndShow("def andThen[A, B, C](f: (A => B), g: (B => C)): A => C")
val res1: String =
def andThen[A, B, C](f: (A => B), g: (B => C)): (A => C) =
  a => g(f(a))

```
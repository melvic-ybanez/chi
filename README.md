# Chi
![release](https://img.shields.io/github/v/release/melvic-ybanez/chi?include_prereleases) ![issues](https://img.shields.io/github/issues/melvic-ybanez/chi) ![loc](https://img.shields.io/tokei/lines/github/melvic-ybanez/chi)
![licence](https://img.shields.io/github/license/melvic-ybanez/chi)

A code generator for universally quantified function signatures. 
Chi stands 
for [Curry-Howard Isomorphism](https://en.wikipedia.org/wiki/Curry%E2%80%93Howard_correspondence). 

This program is currently just a proof-of-concept to see how mechanical functions really
are when their types serve as logical propositions under the Curry-Howard Isomorphism.

Chi takes in a function signature as a string and generates the function implementation.

# Setup and Running Chi
The simplest way to run Chi is by using the stand-alone distribution:
1. Download the jar file from the current [release](https://github.com/melvic-ybanez/chi/releases).
2. Go to your download destination.
3. Run the jar file: `java -jar chi-<version>.jar`

Running Chi will bring in the REPL, within which you can evaluate as many
inputs as you want:

```scala
chi> def identity[A]: A => A
def identity[A]: A => A =
  a => a
  
chi> def apply[A, B]: (A => B) => A => B
def apply[A, B]: ((A => B) => (A => B)) =
  f => a => f(a)
  
chi> def fst[A, B]: (A, B) => A
def fst[A, B]: ((A, B) => A) =
  { case (a, b) =>
    a
  }

chi> def const[A, B]: A => (B => A)
def const[A, B]: (A => (B => A)) =
  a => b => a

chi> def compose[A, B, C]: (B => C) => (A => B) => A => C
def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
  f => g => a => f(g(a))

chi> def andThen[A, B, C]: (A => B) => (B => C) => A => C
def andThen[A, B, C]: ((A => B) => ((B => C) => (A => C))) =
  f => g => a => g(f(a))

chi> def foo[A, B, C]: (A => C) => (B => C) => Either[A, B] => C
def foo[A, B, C]: ((A => C) => ((B => C) => (Either[A, B] => C))) =
  f => g => e => e match {
    case Left(a) => f(a)
    case Right(b) => g(b)
  }

chi> def foo[A]: Either[A, A] => A
def foo[A]: (Either[A, A] => A) =
  e => e match {
    case Left(a) => a
    case Right(a) => a
  }

chi> exit
Bye!
```

Named parameters are also supported:

```scala
chi> def identity(a: A): A
def identity(a: A): A =
  a

chi> def andThen[A, B, C](f: (A => B), g: (B => C)): A => C
def andThen[A, B, C](f: (A => B), g: (B => C)): (A => C) =
  a => g(f(a))

```

# Chi
![release](https://img.shields.io/github/v/release/melvic-ybanez/chi?include_prereleases) ![issues](https://img.shields.io/github/issues/melvic-ybanez/chi) ![loc](https://img.shields.io/tokei/lines/github/melvic-ybanez/chi)
[![License](https://img.shields.io/badge/license-MIT-green)](./LICENSE)

Chi is a code generator for fully parametric functions and functions between selected set of built-in and standard types. 
Chi stands 
for [Curry-Howard Isomorphism](https://en.wikipedia.org/wiki/Curry%E2%80%93Howard_correspondence). 

This program started as a proof-of-concept to see the mechanical nature of the process of 
deriving function implementations when the types involved serve as logical propositions under the Curry-Howard Isomorphism.

Chi takes in a function signature and generates the function implementation. At 
the time of this writing, Chi supports both Java and Scala. Future support for 
other languages are considered.

# Installation
The simplest way to install Chi is to download the distribution from the
[release](https://github.com/melvic-ybanez/chi/releases) page.

# Running Chi
Chi supports both UI and REPL. 

### Running the UI
1. Go to the download destination.
2. Run the jar file: `$ java -jar chi-<version>.jar`

Here's how it currently looks:

https://user-images.githubusercontent.com/4519785/162632565-78b9ee95-ff3f-4032-97e7-82f7beec8bfe.mov

Updated Image:
<img width="1792" alt="Screen Shot 2022-04-11 at 1 31 01 AM" src="https://user-images.githubusercontent.com/4519785/162631911-fbf22095-2022-4108-945c-05d38a9610e9.png">

### Running the REPL
Running the REPL is similar to running the UI except you need to pass an additional
`repl` argument:
`$ java -jar chi-<version>.jar repl`

Within the REPL, inputs are evaluated when you press enter:

```scala
chi> def apply[A, B]: (A => B) => A => B
Detected language: Scala
Generated code:
def apply[A, B]: ((A => B) => (A => B)) =
  identity
  
chi> def fst[A, B]: (A, B) => A
Detected language: Scala
Generated code:
def fst[A, B]: ((A, B) => A) =
  { case (a, b) =>
    a
  }

chi> def const[A, B]: A => (B => A)
Detected language: Scala
Generated code:
def const[A, B]: (A => (B => A)) =
  a => b => a

chi> def compose[A, B, C]: (B => C) => (A => B) => A => C
Detected language: Scala
Generated code:
def compose[A, B, C]: ((B => C) => ((A => B) => (A => C))) =
  f => g => f.compose(g)

chi> def andThen[A, B, C]: (A => B) => (B => C) => A => C
Detected language: Scala
Generated code:
def andThen[A, B, C]: ((A => B) => ((B => C) => (A => C))) =
  f => g => g.compose(f)

chi> def foo[A, B, C]: (A => C) => (B => C) => Either[A, B] => C
Detected language: Scala
Generated code:
def foo[A, B, C]: ((A => C) => ((B => C) => (Either[A, B] => C))) =
  f => g => {
    case Left(a) => f(a)
    case Right(b) => g(b)
  }

chi> def foo[A]: Either[A, A] => A
Detected language: Scala
Generated code:
def foo[A]: (Either[A, A] => A) =
  {
    case Left(a) => a
    case Right(a) => a
  }

chi> def foo[A, B, C]: (A => C) => (B => C) => B => C
Detected language: Scala
Generated code:
def foo[A, B, C]: ((A => C) => ((B => C) => (B => C))) =
  f => identity

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
  g.compose(f)

```

# Supported Languages
As mentioned above, Chi supports both Java and Scala. You only need
to input the signature and Chi will automatically detect the language used
(though it will prioritize Scala syntax)

```scala
chi> def disjunctionElimination[A, B, C](f: A => C, g: B => C): Either[A, B] => C
Detected language: Scala
Generated code:
def disjunctionElimination[A, B, C](
    f: (A => C),
    g: (B => C)
): (Either[A, B] => C) =
  {
    case Left(a) => f(a)
    case Right(b) => g(b)
  }
  
chi> <A, B> B apply(A a, Function<A, B> f)
Detected language: Java
Generated code:
<A, B> B apply(A a, Function<A, B> f) {
    return f.apply(a);
}

chi> <A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g)
Detected language: Java
Generated code:
<A, B, C> Function<A, C> compose(Function<B, C> f, Function<A, B> g) {
    return a -> f.apply(g.apply(a));
}

chi> <A, B, C> BiFunction<A, B, C> foo(Function<A, C> f)
Detected language: Java
Generated code:
<A, B, C> BiFunction<A, B, C> foo(Function<A, C> f) {
    return (a, b) -> {
        return f.apply(a);
    };
}
```

# Built-in types
Chi also recognize built-in types:
```scala
chi> String idString(String s)
Detected language: Java
Generated code:
String idString(String s) {
    return s;
}

chi> def foo(f: String => Int, g: Float => Int): Either[String, Float] => Int
Detected language: Scala
Generated code:
def foo(f: (String => Int), g: (Float => Int)): (Either[String, Float] => Int) =
  {
    case Left(s) => f(s)
    case Right(h) => g(h)
  }
```

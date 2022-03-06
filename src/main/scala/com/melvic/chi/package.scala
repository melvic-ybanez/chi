package com.melvic

package object chi {
  type Result[A] = Either[Error, A]
}

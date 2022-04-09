package com.melvic.chi.views

import java.awt.Component

object FontUtils {
  def updateSize[C <: Component](component: C, size: Float): C =
    update(component, Right(size))

  def updateStyle[C <: Component](component: C, style: Int): C =
    update(component, Left(style))

  def update[C <: Component](component: C, attr: Either[Int, Float]): C = {
    val newFont = attr match {
      case Left(style) => component.getFont.deriveFont(style)
      case Right(size) => component.getFont.deriveFont(size)
    }
    component.setFont(newFont)
    component
  }
}

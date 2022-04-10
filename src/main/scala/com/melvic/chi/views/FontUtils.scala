package com.melvic.chi.views

import com.melvic.chi.Config

import java.awt.{Component, Font}

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

  def withComponentFont[C <: Component](component: C): C =
    updateSize(component, Config.ComponentFontSize)

  def withComponentHeaderFont[C <: Component](component: C) =
    updateStyle(updateSize(component, Config.ComponentHeaderFontSize), Font.BOLD)
}

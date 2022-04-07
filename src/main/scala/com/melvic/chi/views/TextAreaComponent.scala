package com.melvic.chi.views

import org.fife.ui.rsyntaxtextarea.{RSyntaxTextArea, SyntaxConstants, Theme}
import org.fife.ui.rtextarea.RTextScrollPane

import java.awt.Font
import java.io.IOException

class TextAreaComponent extends RSyntaxTextArea(50, 50) {
  setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA)
  setCodeFoldingEnabled(true)

  updateStyle()
  setFont(new Font(getFont.getName, getFont.getStyle, 16))

  private def updateStyle(): Unit =
    try {
      val theme = Theme.load(getClass.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"))
      theme.apply(this)
    } catch {
      case ioe: IOException =>
        ioe.printStackTrace()
    }
}

object TextAreaComponent {
  def withScrollPane = new RTextScrollPane(new TextAreaComponent)
}

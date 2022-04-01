package com.melvic.chi.views

import org.fife.ui.rsyntaxtextarea.{RSyntaxTextArea, Style, SyntaxConstants, Token, TokenTypes}
import org.fife.ui.rtextarea.RTextScrollPane

import java.awt.{Color, Font}

class AreaView extends RSyntaxTextArea(50, 50) {
  setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA)
  setCodeFoldingEnabled(true)

  updateStyle()

  setFont(new Font(getFont.getName, getFont.getStyle, 16))

  import org.fife.ui.rsyntaxtextarea.Theme
  import java.io.IOException

  private def updateStyle(): Unit =
    try {
      val theme = Theme.load(getClass.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"))
      theme.apply(this)
    } catch {
      case ioe: IOException =>
        ioe.printStackTrace()
    }
}

object AreaView {
  def withScrollPane = new RTextScrollPane(new AreaView)
}

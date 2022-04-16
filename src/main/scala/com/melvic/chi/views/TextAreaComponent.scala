package com.melvic.chi.views

import com.melvic.chi.Config
import org.fife.ui.rsyntaxtextarea.{RSyntaxTextArea, SyntaxConstants, Theme, TokenTypes}
import org.fife.ui.rtextarea.RTextScrollPane

import java.awt.{Color, Font}
import java.awt.font.TextAttribute
import java.io.IOException
import scala.collection.mutable
import scala.jdk.CollectionConverters._

class TextAreaComponent extends RSyntaxTextArea(50, 50) {
  setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA)
  setCodeFoldingEnabled(true)
  setAntiAliasingEnabled(true)

  updateStyle()
  updateScheme()
  updateFont()

  private def updateStyle(): Unit =
    try {
      val theme = Theme.load(getClass.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"))
      theme.apply(this)
    } catch {
      case ioe: IOException =>
        ioe.printStackTrace()
    }

  private def updateScheme(): Unit = {
    val scheme = getSyntaxScheme

    setBackground(Color.decode("#212020"))
    setCurrentLineHighlightColor(Color.decode("#191819"))
    scheme.getStyle(TokenTypes.RESERVED_WORD).foreground = Color.decode("#C792EA")
    scheme.getStyle(TokenTypes.IDENTIFIER).foreground = Color.decode("#82AAFF")

    revalidate()
  }

  private def updateFont(): Unit = {
    val font = Font.createFont(Font.TRUETYPE_FONT, getClass.getResourceAsStream(Config.DefaultFontPath))

    val newFont = font.getAttributes.asScala match {
      case attributes: mutable.Map[TextAttribute, Any] =>
        attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON)
        font.deriveFont(attributes.asJava).deriveFont(16: Float)
      case _ => font
    }
    setFont(newFont)
  }
}

object TextAreaComponent {
  def withScrollPane = new RTextScrollPane(new TextAreaComponent)
}

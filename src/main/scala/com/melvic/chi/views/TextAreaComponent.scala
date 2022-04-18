package com.melvic.chi.views

import com.melvic.chi.Config
import org.fife.ui.rsyntaxtextarea.{AbstractTokenMakerFactory, RSyntaxTextArea, SyntaxConstants, Theme, TokenMakerFactory, TokenTypes}
import org.fife.ui.rtextarea.RTextScrollPane

import java.awt.{Color, Font}
import java.awt.font.TextAttribute
import java.io.IOException
import scala.collection.mutable
import scala.jdk.CollectionConverters._

class TextAreaComponent extends RSyntaxTextArea(50, 50) {
  setup()

  updateStyle()
  updateScheme()
  updateFont()

  private def setup(): Unit = {
    val atmf = TokenMakerFactory.getDefaultInstance.asInstanceOf[AbstractTokenMakerFactory]
    atmf.putMapping("text/chi", "com.melvic.chi.views.ChiTokenMaker")
    setSyntaxEditingStyle("text/chi")
    setCodeFoldingEnabled(true)
    setAntiAliasingEnabled(true)
  }

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

    setBackground(Color.decode("#212021"))
    setCurrentLineHighlightColor(Color.decode("#191819"))
    scheme.getStyle(TokenTypes.RESERVED_WORD).foreground = Color.decode("#C792EB")
    scheme.getStyle(TokenTypes.RESERVED_WORD_2).foreground = Color.decode("#C792EB")
    scheme.getStyle(TokenTypes.DATA_TYPE).foreground = Color.decode("#FFCB6A")
    scheme.getStyle(TokenTypes.OPERATOR).foreground = Color.decode("#89DDFE")
    scheme.getStyle(TokenTypes.FUNCTION).foreground = Color.decode("#89DDFE")
    scheme.getStyle(TokenTypes.SEPARATOR).foreground = Color.decode("#89DDFE")
    scheme.getStyle(TokenTypes.IDENTIFIER).foreground = Color.decode("#82AAFE")

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

package com.melvic.chi.views

import com.melvic.chi.Config
import org.fife.ui.rsyntaxtextarea.{RSyntaxTextArea, SyntaxConstants, Theme}
import org.fife.ui.rtextarea.RTextScrollPane
import os.{Path, ResourcePath}

import java.awt.{Font, GraphicsEnvironment}
import java.awt.font.TextAttribute
import java.io.{File, FileInputStream, IOException}
import javax.swing.JTextArea
import scala.collection.mutable
import scala.io.Source
import scala.jdk.CollectionConverters._

class TextAreaComponent extends RSyntaxTextArea(50, 50) {
  setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA)
  setCodeFoldingEnabled(true)

  updateStyle()
  updateFont()

  private def updateStyle(): Unit =
    try {
      val theme = Theme.load(getClass.getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"))
      theme.apply(this)
    } catch {
      case ioe: IOException =>
        ioe.printStackTrace()
    }

  private def updateFont(): Unit = {
    val font = Font.createFont(Font.TRUETYPE_FONT, getClass.getResourceAsStream(Config.DefaultFontPath))

    val newFont = font.getAttributes.asScala match {
      case attributes: mutable.Map[TextAttribute, Any] =>
        attributes.put(TextAttribute.SIZE, 16)
        attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON)
        font.deriveFont(attributes.asJava)
      case _ => font
    }
    setFont(newFont)
  }
}

object TextAreaComponent {
  def withScrollPane = new RTextScrollPane(new TextAreaComponent)
}

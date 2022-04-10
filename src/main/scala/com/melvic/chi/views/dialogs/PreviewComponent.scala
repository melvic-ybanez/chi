package com.melvic.chi.views.dialogs

import com.melvic.chi.config.Preferences
import com.melvic.chi.eval.generateAndShowCode
import com.melvic.chi.views.{FontUtils, TextAreaComponent}
import org.fife.ui.rtextarea.RTextScrollPane

import java.awt.Font
import javax.swing.{JLabel, JPanel}

class PreviewComponent(implicit prefs: Preferences) extends RTextScrollPane(new TextAreaComponent) {
  val textArea = getTextArea
  textArea.setEditable(false)
  textArea.setColumns(70)
  textArea.setRows(20)
  FontUtils.withComponentFont(textArea)

  val inputs = List(
    "def compose[A, B, C](f: B => C, g: A => B): A => C",
    "def disjunctionElimination[A, B, C](f: A => C, g: B => C): Either[A, B] => C",
    "def id[A]: A => A",
    "def identity[A]: A => A"
  )

  run(prefs)

  def run(preferences: Preferences): Unit = {
    val output = inputs.map(generateAndShowCode(_)(preferences)).mkString("\n\n")
    textArea.setText(output)
  }
}

object PreviewComponent {
  def fromPanel(panel: JPanel, extraLabelConstraints: String = "gaptop 20")(
      implicit prefs: Preferences
  ): PreviewComponent = {
    val label = new JLabel("Preview:")
    val fullLabelConstraints = "wrap" +
      (if (extraLabelConstraints.nonEmpty) s", $extraLabelConstraints" else "")
    FontUtils.withComponentFont(FontUtils.updateStyle(label, Font.BOLD))

    val previewComponent = new PreviewComponent()
    panel.add(label, fullLabelConstraints)
    panel.add(previewComponent, "wrap")
    previewComponent
  }
}

package com.melvic.chi.views

import com.melvic.chi.config.Preferences
import com.melvic.chi.{Evaluate, generateAndShowCode, generateAndShowWithInfo}

import java.awt.BorderLayout
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.{JPanel, JSplitPane}

class EditorComponent(implicit prefs: Preferences) extends JPanel {
  val inputView = TextAreaComponent.withScrollPane
  val outputView = {
    val out = TextAreaComponent.withScrollPane
    out.getTextArea.setEditable(false)
    out
  }

  reloadPreferences()

  def reloadPreferences(): Unit = {
    inputView.setLineNumbersEnabled(Preferences.showLineNumbers)
    outputView.setLineNumbersEnabled(Preferences.showLineNumbers)
  }

  private val splitPane = {
    val splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputView, outputView)
    splitPane.setOneTouchExpandable(true)
    splitPane.setResizeWeight(0.5)
    splitPane
  }

  setLayout(new BorderLayout)
  add(splitPane, BorderLayout.CENTER)

  inputView.getTextArea.addKeyListener(new KeyAdapter {
    override def keyReleased(e: KeyEvent): Unit =
      if (prefs.content.editor.evalOnType) run()
  })

  def run(): Unit = {
    val evaluate: Evaluate = if (Preferences.showOutputInfo) generateAndShowWithInfo else generateAndShowCode
    val definitions = inputView.getTextArea.getText.split("\n")
    val outputs = removeComments(definitions)
      .map(_.trim) // we need to trim again to remove extra spaces between a definition and a comment
      .filter(_.nonEmpty)
      .map(evaluate)
    outputView.getTextArea.setText(outputs.mkString("\n\n"))
  }

  def clear(): Unit = {
    inputView.getTextArea.setText("")
    outputView.getTextArea.setText("")
  }

  private def removeComments(definitions: Array[String]): Array[String] =
    definitions
      .map(_.trim)
      .filterNot(_.startsWith("//"))
      // for comments appearing at the end of the definition
      .map { str =>
        val i = str.indexOf("//")
        if (i != -1) str.take(i) else str
      }
}

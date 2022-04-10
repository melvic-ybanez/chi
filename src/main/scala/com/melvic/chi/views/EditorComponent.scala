package com.melvic.chi.views

import com.melvic.chi.{Evaluate, generateAndShowCode, generateAndShowWithInfo}
import com.melvic.chi.config.Preferences

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
    val showLineNumber = prefs.content.editor.showLineNumbers
    inputView.setLineNumbersEnabled(showLineNumber)
    outputView.setLineNumbersEnabled(showLineNumber)
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
    val programs = inputView.getTextArea.getText.split("\n")
    val outputs = programs.map(_.trim).filter(_.nonEmpty).map(evaluate)
    outputView.getTextArea.setText(outputs.mkString("\n\n"))
  }

  def clear(): Unit = {
    inputView.getTextArea.setText("")
    outputView.getTextArea.setText("")
  }
}

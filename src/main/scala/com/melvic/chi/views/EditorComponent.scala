package com.melvic.chi.views

import com.melvic.chi.config.Preferences
import com.melvic.chi.eval.Generate
import com.melvic.chi.{Evaluate, generateAndShowCode, generateAndShowWithInfo, parsers}

import java.awt.BorderLayout
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.{JPanel, JSplitPane}
import scala.annotation.tailrec

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
    val lines = inputView.getTextArea.getText.split("\n")
    val output = Generate.all(lines.toList).mkString("\n\n")
    outputView.getTextArea.setText(output)
  }

  def clear(): Unit = {
    inputView.getTextArea.setText("")
    outputView.getTextArea.setText("")
  }
}

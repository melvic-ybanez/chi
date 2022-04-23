package com.melvic.chi.views

import com.melvic.chi.config.Preferences
import com.melvic.chi.eval.Generate

import java.awt.BorderLayout
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.{JPanel, JSplitPane}

class EditorComponent(implicit prefs: Preferences) extends JPanel {
  val textAreaScroll = TextAreaComponent.withScrollPane
  val outputScroll = {
    val out = TextAreaComponent.withScrollPane
    out.getTextArea.setEditable(false)
    out
  }

  reloadPreferences()

  setLayout(new BorderLayout)
  add(splitPane, BorderLayout.CENTER)

  def reloadPreferences(): Unit = {
    textAreaScroll.setLineNumbersEnabled(Preferences.showLineNumbers)
    outputScroll.setLineNumbersEnabled(Preferences.showLineNumbers)
  }

  private lazy val splitPane = {
    val splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textAreaScroll, outputScroll)
    splitPane.setOneTouchExpandable(true)
    splitPane.setResizeWeight(0.5)
    splitPane
  }

  textAreaScroll.getTextArea.addKeyListener(new KeyAdapter {
    override def keyReleased(e: KeyEvent): Unit =
      if (prefs.content.editor.evalOnType) run()
  })

  def setInputText(text: String): Unit = {
    textAreaScroll.getTextArea.setText(text)
  }

  def outputText: String = outputScroll.getTextArea.getText

  def run(): Unit = {
    val lines = textAreaScroll.getTextArea.getText.split("\n")
    val output = Generate.allToString(lines.toList)
    outputScroll.getTextArea.setText(output)
  }

  def clear(): Unit = {
    textAreaScroll.getTextArea.setText("")
    outputScroll.getTextArea.setText("")
  }
}

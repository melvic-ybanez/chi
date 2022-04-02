package com.melvic.chi.views

import com.melvic.chi.Evaluate

import java.awt.BorderLayout
import java.awt.event.{KeyEvent, KeyListener}
import javax.swing.{JPanel, JSplitPane}

class EditorView(evaluate: Evaluate) extends JPanel {
  val inputView = AreaView.withScrollPane
  val outputView = {
    val out = AreaView.withScrollPane
    out.getTextArea.setEditable(false)
    out
  }

  private val splitPane = {
    val splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputView, outputView)
    splitPane.setOneTouchExpandable(true)
    splitPane.setResizeWeight(0.5)
    splitPane
  }

  setLayout(new BorderLayout)
  add(splitPane, BorderLayout.CENTER)

  inputView.getTextArea.addKeyListener(new KeyListener {
    override def keyTyped(e: KeyEvent): Unit = {}

    override def keyPressed(e: KeyEvent): Unit = {}

    override def keyReleased(e: KeyEvent): Unit = run()
  })

  def run(): Unit = outputView.getTextArea.setText(evaluate(inputView.getTextArea.getText))
}


package com.melvic.chi.views

import java.awt.BorderLayout
import javax.swing.{JPanel, JSplitPane}

class EditorView extends JPanel {
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
}


package com.melvic.chi.views

import java.awt.BorderLayout
import javax.swing.{JPanel, JSplitPane}

class EditorView extends JPanel {
  val inputView = AreaView.withScrollPane
  val outputView = AreaView.withScrollPane

  outputView.getTextArea.setEditable(false)

  private val splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputView, outputView)
  splitPane.setOneTouchExpandable(true)
  splitPane.setResizeWeight(0.5)

  setLayout(new BorderLayout)
  add(splitPane, BorderLayout.CENTER)
}

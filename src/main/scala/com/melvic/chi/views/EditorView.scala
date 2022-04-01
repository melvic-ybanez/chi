package com.melvic.chi.views

import javax.swing._
import javax.swing.JFrame._
import java.awt._
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.BorderLayout

class EditorView extends JFrame {
  private val serialVersionUID = 1L

  val mainPane = new JPanel(new BorderLayout)
  val scrollPane = new RTextScrollPane(new AreaView)
  mainPane.add(scrollPane)

  setContentPane(mainPane)
  setTitle("Chi")
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  pack()
  setLocationRelativeTo(null)
  setExtendedState(Frame.MAXIMIZED_BOTH)
}

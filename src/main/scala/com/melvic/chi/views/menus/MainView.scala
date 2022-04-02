package com.melvic.chi.views.menus

import com.melvic.chi.Evaluate
import com.melvic.chi.views.EditorView

import java.awt._
import javax.swing._

class MainView(evaluate: Evaluate) extends JFrame {
  val editorView = new EditorView(evaluate)

  setContentPane(editorView)
  setTitle("Chi")
  setJMenuBar(new MenuBarView(editorView))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  pack()
  setLocationRelativeTo(null)
  setExtendedState(Frame.MAXIMIZED_BOTH)
}

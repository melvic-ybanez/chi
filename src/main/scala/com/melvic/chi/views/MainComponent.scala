package com.melvic.chi.views

import com.melvic.chi.Evaluate
import com.melvic.chi.views.menus.MenuBar

import java.awt._
import javax.swing._

class MainComponent(evaluate: Evaluate) extends JFrame {
  val editorView = new EditorComponent(evaluate)

  setContentPane(editorView)
  setTitle("Chi")
  setJMenuBar(new MenuBar(this, editorView))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  pack()
  setLocationRelativeTo(null)
  setExtendedState(Frame.MAXIMIZED_BOTH)
}

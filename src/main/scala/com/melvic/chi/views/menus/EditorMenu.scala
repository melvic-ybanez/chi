package com.melvic.chi.views.menus

import com.melvic.chi.views.EditorView

import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuItem, KeyStroke}

class EditorMenu(editorView: EditorView) extends JMenu("Editor") {
  add(runMenuItem)
  add(clearMenuItem)

  private def runMenuItem = {
    val runMenuItem = new JMenuItem("Run")
    runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK))
    runMenuItem.addActionListener(_ => editorView.run())
    runMenuItem
  }

  private def clearMenuItem = {
    val clearMenuItem = new JMenuItem("Clear")
    clearMenuItem.addActionListener(_ => editorView.clear())
    clearMenuItem
  }
}

package com.melvic.chi.views.menus

import com.melvic.chi.Evaluate
import com.melvic.chi.views.EditorView

import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuBar, JMenuItem, KeyStroke}

class MenuBarView(editorView: EditorView) extends JMenuBar {
  val fileMenu = createMenuItem(
    new JMenu("File"),
    "Exit",
    Some(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK))
  )

  def createMenuItem(menu: JMenu, itemName: String, keyStroke: Option[KeyStroke] = None): JMenu = {
    val menuItem = new JMenuItem(itemName)
    keyStroke.foreach(menuItem.setAccelerator)
    menu.add(menuItem)
    menu
  }

  add(fileMenu)
  add(new EditorMenu(editorView))
}
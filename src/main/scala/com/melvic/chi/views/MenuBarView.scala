package com.melvic.chi.views

import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuBar, JMenuItem, KeyStroke}

class MenuBarView extends JMenuBar {
  val fileMenu = createMenuItem(
    new JMenu("File"),
    "Exit",
    Some(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK))
  )

  val editorMenu = createMenuItem(
    createMenuItem(
      new JMenu("Editor"),
      "Run",
      Some(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK))
    ),
    "Clear",
    None
  )

  def createMenuItem(menu: JMenu, itemName: String, keyStroke: Option[KeyStroke] = None): JMenu = {
    val menuItem = new JMenuItem(itemName)
    keyStroke.foreach(menuItem.setAccelerator)
    menu.add(menuItem)
    menu
  }

  add(fileMenu)
  add(editorMenu)
}

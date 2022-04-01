package com.melvic.chi.views

import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuBar, JMenuItem, KeyStroke}

class MenuBarView extends JMenuBar {
  val fileMenu = {
    val fileMenu = new JMenu("File")
    val exitMeuItem = new JMenuItem("Exit")
    exitMeuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK))
    fileMenu.add(exitMeuItem)
    fileMenu
  }

  add(fileMenu)
}

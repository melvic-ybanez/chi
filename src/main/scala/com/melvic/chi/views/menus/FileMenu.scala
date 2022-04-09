package com.melvic.chi.views.menus

import com.melvic.chi.config.Preferences
import com.melvic.chi.views.PreferencesDialog

import java.awt.Frame
import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuItem, KeyStroke}

class FileMenu(frame: Frame)(implicit preferences: Preferences) extends JMenu("File") {
  add(preferencesMenuItem)
  add(exitMenuItem)

  val prefsDialog = new PreferencesDialog(frame)

  private def preferencesMenuItem = {
    val prefsItem = new JMenuItem("Preferences")
    prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK))
    prefsItem.addActionListener(_ => prefsDialog.display())
    prefsItem
  }

  private def exitMenuItem = {
    val exitItem = new JMenuItem("Exit")
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK))
    exitItem
  }
}

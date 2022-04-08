package com.melvic.chi.views.menus

import com.melvic.chi.config.Preferences
import com.melvic.chi.views.EditorComponent

import java.awt.Frame
import java.awt.event.{ActionEvent, KeyEvent}
import javax.swing.{JMenu, JMenuBar, JMenuItem, KeyStroke}

class MenuBar(frame: Frame, editorView: EditorComponent)(implicit prefs: Preferences) extends JMenuBar {
  add(new FileMenu(frame))
  add(new EditorMenu(editorView))
}

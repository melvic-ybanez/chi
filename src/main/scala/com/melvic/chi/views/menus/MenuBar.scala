package com.melvic.chi.views.menus

import com.melvic.chi.config.Preferences
import com.melvic.chi.views.EditorComponent
import com.melvic.chi.views.dialogs.PreferencesDialog

import java.awt.Frame
import javax.swing.JMenuBar

class MenuBar(frame: Frame, editorView: EditorComponent, prefsDialog: PreferencesDialog)(
    implicit prefs: Preferences
) extends JMenuBar {
  add(new FileMenu(frame, prefsDialog))
  add(new EditorMenu(editorView))
}

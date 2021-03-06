package com.melvic.chi.views

import com.melvic.chi.config.Preferences
import com.melvic.chi.views.menus.MenuBar
import com.melvic.chi.views.prefs.PreferencesDialog

import java.awt._
import javax.swing._

class MainWindow(implicit preferences: Preferences) extends JFrame {
  val editorComponent = new EditorComponent
  val preferencesDialog = new PreferencesDialog(this, editorComponent)

  setTitle("Chi")
  createContentPane(editorComponent)
  setJMenuBar(new MenuBar(this, editorComponent, preferencesDialog))
  setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  pack()
  setLocationRelativeTo(null)
  setExtendedState(Frame.MAXIMIZED_BOTH)

  def createContentPane(editorComponent: EditorComponent): Unit = {
    val mainComponent = new JPanel
    mainComponent.setLayout(new BorderLayout())
    mainComponent.add(new ToolBarComponent(editorComponent, preferencesDialog), BorderLayout.PAGE_START)
    mainComponent.add(editorComponent, BorderLayout.CENTER)
    setContentPane(mainComponent)
  }
}

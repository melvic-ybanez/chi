package com.melvic.chi.views.prefs

import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.views.FontUtils

import java.awt.{Dimension, Frame}
import javax.swing._

class PreferencesDialog(frame: Frame)(implicit preferences: Preferences) extends JDialog(frame, true) {
  setTitle("Preferences")

  val scalaSettingsComponent = new ScalaSettingsComponent()
  val editorSettingsComponent = new EditorSettingsComponent()
  val mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT)

  setContentPane(optionPane)
  pack()

  def display(): Unit = {
    setLocationRelativeTo(frame)
    preferences.reload()
    reloadPreferences()
    setVisible(true)
  }

  def reloadPreferences(): Unit = {
    scalaSettingsComponent.reloadPreferences(preferences.content.scala)
    editorSettingsComponent.reloadPreferences(preferences.content.editor)
  }

  def showEditorSettings(): Unit = showSettings(editorSettingsComponent)

  def showScalaSettings(): Unit = showSettings(scalaSettingsComponent)

  def showSettings(settings: JPanel): Unit = {
    val settingsScroll = new JScrollPane(settings)
    mainSplitPane.setRightComponent(settingsScroll)
  }

  private def configureMainPane: JSplitPane = {
    val treeView = new JScrollPane(SettingsTree.fromPreferencesDialog(this))
    treeView.setPreferredSize(new Dimension(150, treeView.getPreferredSize.height))
    mainSplitPane.setDividerLocation(150)
    mainSplitPane.setLeftComponent(treeView)
    mainSplitPane
  }

  private def optionPane: JOptionPane = {
    val applyButtonString = "Apply"
    val cancelButtonString = "Cancel"

    val optionPane = new JOptionPane(
      configureMainPane,
      JOptionPane.PLAIN_MESSAGE,
      JOptionPane.YES_NO_OPTION,
      null,
      Array(applyButtonString, cancelButtonString),
      applyButtonString
    )

    optionPane.addPropertyChangeListener { event =>
      val prop = event.getPropertyName
      if (isVisible && event.getSource == optionPane &&
          (JOptionPane.VALUE_PROPERTY == prop || JOptionPane.INPUT_VALUE_PROPERTY == prop)) {
        val value = optionPane.getValue
        if (value == JOptionPane.UNINITIALIZED_VALUE) ()
        else {
          // Reset the value so the next click on the same button won't be ignored
          optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE)

          if (value == applyButtonString) {
            val settings = preferences.content.copy(
              scala = ScalaSettings(
                scalaSettingsComponent.pointFreeBox.isSelected,
                scalaSettingsComponent.simplifyMatchBox.isSelected,
                scalaSettingsComponent.usePredefBox.isSelected
              )
            )
            preferences.save(settings)
          }
        }
        setVisible(false)
      }
    }

    optionPane
  }
}

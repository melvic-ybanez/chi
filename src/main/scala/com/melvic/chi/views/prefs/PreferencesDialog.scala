package com.melvic.chi.views.prefs

import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.views.FontUtils

import java.awt.Frame
import javax.swing._

class PreferencesDialog(frame: Frame)(implicit preferences: Preferences) extends JDialog(frame, true) {
  setTitle("Preferences")

  val scalaComponent = ScalaSettingsComponent.build

  setContentPane(optionPane)
  pack()

  def display(): Unit = {
    setLocationRelativeTo(frame)
    preferences.reload()
    reloadPreferences()
    setVisible(true)
  }

  def reloadPreferences(): Unit = {
    scalaComponent.reloadPreferences(preferences.content.scala)
  }

  private def optionPane: JOptionPane = {
    val applyButtonString = "Apply"
    val cancelButtonString = "Cancel"

    val optionPane = new JOptionPane(
      scalaComponent,
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
                scalaComponent.pointFreeBox.isSelected,
                scalaComponent.simplifyMatchBox.isSelected,
                scalaComponent.usePredefBox.isSelected
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

object PreferencesDialog {
  def addTitle(title: String, panel: JPanel): Unit = {
    panel.add(
      FontUtils.withComponentHeaderFont(new JLabel(title)),
      "gapleft 20, wrap"
    )
    panel.add(new JSeparator(), "growx, wrap, gapbottom 20")
  }
}

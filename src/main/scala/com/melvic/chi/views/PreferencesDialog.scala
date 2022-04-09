package com.melvic.chi.views

import com.melvic.chi.Config
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.config.{Preferences, SettingsContent}
import net.miginfocom.swing.MigLayout

import java.awt.{Font, Frame}
import javax.swing._

class PreferencesDialog(frame: Frame)(implicit preferences: Preferences)
    extends JDialog(frame, true) {
  setTitle("Preferences")

  val pointFreeBox = createCheckBox("Point-free style")
  val simplifyMatchBox = createCheckBox("Simplify match statements")
  val usePredefBox = createCheckBox(
    "Use Predef and Function utilities when applicable"
  )

  setContentPane(optionPane)
  pack()

  def display(): Unit = {
    setLocationRelativeTo(frame)
    preferences.reload()
    reloadPreferences()
    setVisible(true)
  }

  def reloadPreferences(): Unit = {
    val SettingsContent(ScalaSettings(pointFree, simplifyMatch, usePredef)) = preferences.content
    pointFreeBox.setSelected(pointFree)
    simplifyMatchBox.setSelected(simplifyMatch)
    usePredefBox.setSelected(usePredef)
  }

  private def optionPane: JOptionPane = {
    val applyButtonString = "Apply"
    val cancelButtonString = "Cancel"

    val optionPane = new JOptionPane(
      scalaSettingsComponent(pointFreeBox :: simplifyMatchBox :: usePredefBox :: Nil),
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
            val settings = SettingsContent(
              ScalaSettings(
                pointFreeBox.isSelected,
                simplifyMatchBox.isSelected,
                usePredefBox.isSelected
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

  private def scalaSettingsComponent(checkBoxes: List[JCheckBox]): JPanel = {
    val scalaPane = new JPanel()
    scalaPane.setLayout(new MigLayout())

    addTitle("Scala Settings", scalaPane)
    checkBoxes.foreach(scalaPane.add(_, "wrap"))

    scalaPane
  }

  private def createCheckBox(text: String): JCheckBox =
    FontUtils.updateSize(new JCheckBox(text), Config.DialogFontSize)

  private def addTitle(title: String, panel: JPanel): Unit = {
    panel.add(
      FontUtils.updateStyle(
        FontUtils.updateSize(new JLabel(title), Config.DialogHeaderFontSize),
        Font.BOLD
      ),
      "gapleft 20, wrap"
    )
    panel.add(new JSeparator(), "growx, wrap, gapbottom 20")
  }
}

package com.melvic.chi.views.dialogs

import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.config.{Preferences, SettingsContent}
import com.melvic.chi.views.FontUtils
import net.miginfocom.swing.MigLayout

import java.awt.Frame
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import javax.swing._
import scala.Function.const

class PreferencesDialog(frame: Frame)(implicit preferences: Preferences) extends JDialog(frame, true) {
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

    val previewComponent = PreviewComponent.fromPanel(scalaPane)
    addCheckBoxListeners(previewComponent)
    scalaPane
  }

  private def createCheckBox(text: String): JCheckBox =
    FontUtils.withComponentFont(new JCheckBox(text))

  private def addTitle(title: String, panel: JPanel): Unit = {
    panel.add(
      FontUtils.withComponentHeaderFont(new JLabel(title)),
      "gapleft 20, wrap"
    )
    panel.add(new JSeparator(), "growx, wrap, gapbottom 20")
  }

  private def addCheckBoxListeners(previewComponent: PreviewComponent): Unit = {
    def rerunPreview(): Unit = {
      val scalaSettings = preferences.content.scala
      val newScalaSettings = scalaSettings.copy(
        pointFree = pointFreeBox.isSelected,
        simplifyMatch = simplifyMatchBox.isSelected,
        usePredef = usePredefBox.isSelected
      )
      val newPreferences = Preferences.fromContent(SettingsContent(newScalaSettings))
      println(newScalaSettings)
      previewComponent.run(newPreferences)
    }

    pointFreeBox.addActionListener(_ => rerunPreview())
    pointFreeBox.addItemListener(_ => rerunPreview())
    simplifyMatchBox.addActionListener(_ => rerunPreview())
    simplifyMatchBox.addItemListener(_ => rerunPreview())
    usePredefBox.addActionListener(_ => rerunPreview())
    usePredefBox.addItemListener(_ => rerunPreview())
  }
}

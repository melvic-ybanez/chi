package com.melvic.chi.views.prefs

import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.views.FontUtils
import net.miginfocom.swing.MigLayout

import javax.swing.{JCheckBox, JPanel}

class ScalaSettingsComponent(implicit preferences: Preferences) extends JPanel {
  val pointFreeBox = createCheckBox("Point-free style")
  val simplifyMatchBox = createCheckBox("Simplify match statements")
  val usePredefBox = createCheckBox(
    "Use Predef and Function utilities when applicable"
  )

  def reloadPreferences(scalaSettings: ScalaSettings): Unit = {
    val ScalaSettings(pointFree, simplifyMatch, usePredef) = scalaSettings
    pointFreeBox.setSelected(pointFree)
    simplifyMatchBox.setSelected(simplifyMatch)
    usePredefBox.setSelected(usePredef)
  }

  private def createCheckBox(text: String): JCheckBox =
    FontUtils.withComponentFont(new JCheckBox(text))

  private def addCheckBoxListeners(
      previewComponent: PreviewComponent
  )(implicit preferences: Preferences): Unit = {
    def rerunPreview(): Unit = {
      val scalaSettings = preferences.content.scala
      val settingsContent = preferences.content.copy(
        scala = scalaSettings.copy(
          pointFree = pointFreeBox.isSelected,
          simplifyMatch = simplifyMatchBox.isSelected,
          usePredef = usePredefBox.isSelected
        )
      )
      val newPreferences = Preferences.fromContent((settingsContent))
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

object ScalaSettingsComponent {
  def build(implicit preferences: Preferences): ScalaSettingsComponent = {
    val scalaPane = new ScalaSettingsComponent()

    import scalaPane._

    scalaPane.setLayout(new MigLayout())

    PreferencesDialog.addTitle("Scala Settings", scalaPane)
    val checkBoxes = pointFreeBox :: simplifyMatchBox :: usePredefBox :: Nil
    checkBoxes.foreach(scalaPane.add(_, "wrap"))

    val previewComponent = PreviewComponent.fromPanel(scalaPane)
    addCheckBoxListeners(previewComponent)
    scalaPane
  }
}

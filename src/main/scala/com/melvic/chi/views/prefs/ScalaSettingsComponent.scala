package com.melvic.chi.views.prefs

import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.ScalaSettings
import com.melvic.chi.views.prefs.Utils.createCheckBox
import net.miginfocom.swing.MigLayout

import javax.swing.JPanel

class ScalaSettingsComponent(implicit preferences: Preferences) extends JPanel {
  val pointFreeBox = createCheckBox("Point-free style")
  val simplifyMatchBox = createCheckBox("Simplify match statements")
  val usePredefBox = createCheckBox(
    "Use Predef and Function utilities when applicable"
  )

  init()

  private def init(): Unit = {
    setLayout(new MigLayout())

    Utils.addTitle("Scala Settings", this)
    val checkBoxes = pointFreeBox :: simplifyMatchBox :: usePredefBox :: Nil
    checkBoxes.foreach(add(_, "wrap"))

    val previewComponent = PreviewComponent.fromPanel(this, showExtraInfo = false)
    addCheckBoxListeners(previewComponent)
  }

  def reloadPreferences(scalaSettings: ScalaSettings): Unit = {
    val ScalaSettings(pointFree, simplifyMatch, usePredef) = scalaSettings
    pointFreeBox.setSelected(pointFree)
    simplifyMatchBox.setSelected(simplifyMatch)
    usePredefBox.setSelected(usePredef)
  }

  private def addCheckBoxListeners(previewComponent: PreviewComponent): Unit = {
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

    pointFreeBox.addItemListener(_ => rerunPreview())
    simplifyMatchBox.addItemListener(_ => rerunPreview())
    usePredefBox.addItemListener(_ => rerunPreview())
  }
}

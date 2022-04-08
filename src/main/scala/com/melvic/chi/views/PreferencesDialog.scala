package com.melvic.chi.views

import com.melvic.chi.Config
import net.miginfocom.swing.MigLayout

import java.awt.{Font, Frame}
import javax.swing._

class PreferencesDialog(frame: Frame) extends JDialog(frame, true) {
  setTitle("Preferences")

  setContentPane(
    new JOptionPane(
      createScalaPrefsComponent(),
      JOptionPane.PLAIN_MESSAGE,
      JOptionPane.YES_NO_OPTION,
      null,
      Array("Apply", "Cancel"),
      "Apply"
    )
  )
  pack()

  private def createScalaPrefsComponent(): JPanel = {
    val pointFreeBox = createCheckBox("Point-free style")
    val simplyMatchBox = createCheckBox("Simplify match statements")
    val usePredefBox = createCheckBox("Use Predef and Function utilities when applicable")

    val scalaPane = new JPanel()
    scalaPane.setLayout(new MigLayout())

    addTitle("Scala Settings", scalaPane)
    scalaPane.add(pointFreeBox, "wrap")
    scalaPane.add(simplyMatchBox, "wrap")
    scalaPane.add(usePredefBox, "wrap")

    scalaPane
  }

  private def createCheckBox(text: String): JCheckBox =
    FontUtils.updateSize(new JCheckBox(text), Config.DialogFontSize)

  private def addTitle(title: String, panel: JPanel): Unit = {
    panel.add(
      FontUtils.updateStyle(FontUtils.updateSize(new JLabel(title), Config.DialogHeaderFontSize), Font.BOLD),
      "gapleft 20, wrap"
    )
    panel.add(new JSeparator(), "growx, wrap, gapbottom 20")
  }
}

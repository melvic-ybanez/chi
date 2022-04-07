package com.melvic.chi.views

import com.melvic.chi.Config
import net.miginfocom.swing.MigLayout

import java.awt.{Font, Frame}
import javax.swing.border.Border
import javax.swing.{BorderFactory, JCheckBox, JDialog, JFrame, JOptionPane, JPanel}

class PreferencesDialog(frame: Frame) extends JDialog(frame, true) {
  setTitle("Preferences")
  setLayout(new MigLayout())

  add(createScalaPrefsComponent(), "wrap")

  pack()

  private def createScalaPrefsComponent(): JPanel = {
    val pointFreeBox = createCheckBox("Point-free style")
    val simplyMatchBox = createCheckBox("Simplify match statements")
    val usePredefBox = createCheckBox("Use Predef and Function utilities when applicable")

    val scalaPane = new JPanel()
    scalaPane.setLayout(new MigLayout())
    scalaPane.setBorder(createBorder("Scala"))

    scalaPane.add(pointFreeBox, "wrap")
    scalaPane.add(simplyMatchBox, "wrap")
    scalaPane.add(usePredefBox, "wrap")

    scalaPane
  }

  private def createCheckBox(text: String): JCheckBox = {
    val checkBox = new JCheckBox(text)
    checkBox.setFont(new Font(checkBox.getFont.getName, checkBox.getFont.getStyle, Config.DialogFontSize))
    checkBox
  }

  private def createBorder(title: String): Border = {
    val border = BorderFactory.createTitledBorder(title)
    val titleFont = border.getTitleFont
    border.setTitleFont(new Font(titleFont.getName, titleFont.getStyle, Config.DialogFontSize))
    border
  }
}

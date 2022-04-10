package com.melvic.chi.views.prefs

import com.melvic.chi.views.FontUtils

import java.text.NumberFormat
import javax.swing._
import javax.swing.text.NumberFormatter

object Utils {
  def createCheckBox(text: String): JCheckBox =
    FontUtils.withComponentFont(new JCheckBox(text))

  def addTitle(title: String, panel: JPanel): Unit = {
    panel.add(
      FontUtils.withComponentHeaderFont(new JLabel(title)),
      "gapleft 20, wrap"
    )
    panel.add(new JSeparator(), "growx, wrap, gapbottom 20")
  }

  def createNumberFormattedTextField: JFormattedTextField = {
    val formatter = new NumberFormatter(NumberFormat.getInstance())
    formatter.setValueClass(classOf[Integer])
    formatter.setMaximum(0)
    formatter.setMaximum(Int.MaxValue)
    formatter.setAllowsInvalid(false)
    formatter.setCommitsOnValidEdit(true)
    new JFormattedTextField(formatter)
  }
}

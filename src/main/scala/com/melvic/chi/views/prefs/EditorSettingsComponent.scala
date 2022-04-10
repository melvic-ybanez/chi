package com.melvic.chi.views.prefs

import com.melvic.chi.config.Preferences
import com.melvic.chi.config.SettingsContent.EditorSettings
import com.melvic.chi.views.prefs.Utils.createCheckBox
import net.miginfocom.swing.MigLayout

import java.awt.event.{KeyAdapter, KeyEvent, KeyListener}
import javax.swing.{JLabel, JPanel}

class EditorSettingsComponent(implicit preferences: Preferences) extends JPanel {
  val evalOnTypeBox = createCheckBox("Evaluate code while user is typing")
  val showLineNumbersBox = createCheckBox("Show line numbers")
  val maxColumnField = Utils.createNumberFormattedTextField
  val showOutputInfoBox = createCheckBox("Show extra output information")

  init()

  private def init(): Unit = {
    setLayout(new MigLayout())

    Utils.addTitle("Editor Settings", this)
    val components = evalOnTypeBox :: showLineNumbersBox :: showOutputInfoBox :: Nil
    components.foreach(add(_, "wrap"))

    add(new JLabel("Maximum column for the output:"), "split 2")
    add(maxColumnField, "wrap")
    maxColumnField.setColumns(5)

    val previewComponent = PreviewComponent.fromPanel(this)

    reloadPreferences(preferences.content.editor)
    addListeners(previewComponent)
  }

  def reloadPreferences(editorSettings: EditorSettings): Unit = {
    val EditorSettings(evalOnType, showLineNumber, maxColumn, showOutputInfo) = editorSettings
    evalOnTypeBox.setSelected(evalOnType)
    showLineNumbersBox.setSelected(showLineNumber)
    maxColumnField.setText(maxColumn.toString)
    showOutputInfoBox.setSelected(showOutputInfo)
  }

  private def addListeners(previewComponent: PreviewComponent): Unit = {
    def rerunPreview(): Unit = {
      val editorSettings = preferences.content.editor
      val settingsContent = preferences.content.copy(
        editor = editorSettings.copy(
          evalOnType = evalOnTypeBox.isSelected,
          showLineNumbers = showOutputInfoBox.isSelected,
          maxColumn = maxColumnField.getText.toInt,
          showOutputInfo = showOutputInfoBox.isSelected
        )
      )
      val newPreferences = Preferences.fromContent(settingsContent)
      previewComponent.run(newPreferences)
    }

    evalOnTypeBox.addItemListener(_ => rerunPreview())
    showLineNumbersBox.addItemListener(_ => rerunPreview())
    maxColumnField.addKeyListener(new KeyAdapter {
      override def keyReleased(e: KeyEvent): Unit = rerunPreview()
    })
    showOutputInfoBox.addItemListener(_ => rerunPreview())
  }
}

package com.melvic.chi.views

import com.melvic.chi.Config

import java.awt.event.ActionListener
import javax.swing.{ImageIcon, JButton, JToolBar}

class ToolBarComponent(editorComponent: EditorComponent, preferencesDialog: PreferencesDialog) extends JToolBar {
  add(makeButton("run", "Run code", "Run", _ => editorComponent.run()))
  add(makeButton("clear", "Clear all text", "Clear", _ => editorComponent.clear()))
  add(makeButton("preferences", "Show Preferences", "Preferences", _ => preferencesDialog.display()))

  private def makeButton(
      imageName: String,
      tooltipText: String,
      altText: String,
      actionListener: ActionListener
  ): JButton = {
    val button = new JButton()
    button.setToolTipText(tooltipText)
    button.addActionListener(actionListener)

    val imageURL = getClass.getResource(Config.IconsDir + imageName + ".png")
    if (imageURL != null)
      button.setIcon(new ImageIcon(imageURL, altText))
    else {
      button.setText(altText)
      System.err.println("Icon not found: " + imageName)
    }

    button
  }
}

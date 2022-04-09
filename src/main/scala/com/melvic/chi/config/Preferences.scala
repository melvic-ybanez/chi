package com.melvic.chi.config

import com.melvic.chi.config.SettingsContent.save

class Preferences {
  var content: SettingsContent = SettingsContent.dummy

  def reload(): Unit =
    content = SettingsContent.load

  def save(settingsContent: SettingsContent): Unit = {
    SettingsContent.save(settingsContent)
    reload()
  }
}

object Preferences {
  def load: Preferences = new Preferences {
    content = SettingsContent.load
  }
}

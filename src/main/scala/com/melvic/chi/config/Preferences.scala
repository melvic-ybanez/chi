package com.melvic.chi.config

class Preferences {
  var content: PrefsContent = PrefsContent.dummy

  def reload(): Unit =
    content = PrefsContent.load
}

object Preferences {
  def load: Preferences = new Preferences {
    content = PrefsContent.load
  }
}

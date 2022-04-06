package com.melvic.chi.config

import com.melvic.chi.config.Preferences.ScalaConfig
import upickle.default._

final case class Preferences(scala: ScalaConfig)

object Preferences {
  final case class ScalaConfig(pointFree: Boolean, simplifyMatch: Boolean, usePredef: Boolean)

  implicit val scalaConfigRW: ReadWriter[ScalaConfig] = macroRW[ScalaConfig]
  implicit val prefRW: ReadWriter[Preferences] = macroRW[Preferences]

  var preferences: Preferences = Preferences(
    ScalaConfig(pointFree = false, simplifyMatch = false, usePredef = false)
  )

  def cache(preferences: Preferences): Unit = {
    Preferences.preferences = preferences
  }

  def load: Preferences = {
    val prefString =
      if (os.exists(os.pwd / "user-prefs.json")) os.read(os.pwd / "user-prefs.json")
      else os.read(os.resource / "preferences.json")
    val preferences = read[Preferences](ujson.read(prefString))
    cache(preferences)
    preferences
  }
}

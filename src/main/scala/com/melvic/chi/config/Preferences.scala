package com.melvic.chi.config

import com.melvic.chi.config.Preferences.ScalaPrefs
import upickle.default._

final case class Preferences(scala: ScalaPrefs)

object Preferences {
  final case class ScalaPrefs(pointFree: Boolean, simplifyMatch: Boolean, usePredef: Boolean)

  implicit def scalaPrefs(implicit prefs: Preferences): ScalaPrefs =
    prefs.scala

  implicit val scalaConfigRW: ReadWriter[ScalaPrefs] = macroRW[ScalaPrefs]
  implicit val prefRW: ReadWriter[Preferences] = macroRW[Preferences]

  var preferences: Preferences = Preferences(
    ScalaPrefs(pointFree = false, simplifyMatch = false, usePredef = false)
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

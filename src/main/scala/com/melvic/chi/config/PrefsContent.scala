package com.melvic.chi.config

import com.melvic.chi.config.PrefsContent.ScalaPrefs
import upickle.default._

final case class PrefsContent(scala: ScalaPrefs)

object PrefsContent {
  final case class ScalaPrefs(pointFree: Boolean, simplifyMatch: Boolean, usePredef: Boolean)

  implicit def scalaPrefs(implicit prefs: Preferences): ScalaPrefs =
    prefs.content.scala

  implicit val scalaConfigRW: ReadWriter[ScalaPrefs] = macroRW[ScalaPrefs]
  implicit val prefRW: ReadWriter[PrefsContent] = macroRW[PrefsContent]

  val dummy = PrefsContent(
    ScalaPrefs(pointFree = false, simplifyMatch = false, usePredef = false)
  )

  def load: PrefsContent = {
    val prefString =
      if (os.exists(os.pwd / "user-prefs.json")) os.read(os.pwd / "user-prefs.json")
      else os.read(os.resource / "preferences.json")
    val preferences = read[PrefsContent](ujson.read(prefString))
    preferences
  }
}

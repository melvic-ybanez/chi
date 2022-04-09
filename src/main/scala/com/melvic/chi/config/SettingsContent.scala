package com.melvic.chi.config

import com.melvic.chi.Config
import com.melvic.chi.config.SettingsContent.ScalaSettings
import upickle.default._

import java.io.{BufferedWriter, FileWriter}

final case class SettingsContent(scala: ScalaSettings)

object SettingsContent {
  final case class ScalaSettings(pointFree: Boolean, simplifyMatch: Boolean, usePredef: Boolean)

  implicit def scalaPrefs(implicit prefs: Preferences): ScalaSettings =
    prefs.content.scala

  implicit val scalaConfigRW: ReadWriter[ScalaSettings] = macroRW[ScalaSettings]
  implicit val prefRW: ReadWriter[SettingsContent] = macroRW[SettingsContent]

  val dummy = SettingsContent(
    ScalaSettings(pointFree = false, simplifyMatch = false, usePredef = false)
  )

  val customSettingsPath = os.pwd / Config.CustomSettingsPath
  val defaultSettingsPath = os.resource / Config.DefaultSettingsPath

  def load: SettingsContent = {
    val prefPath =
      if (os.exists(customSettingsPath)) os.read(customSettingsPath)
      else os.read(defaultSettingsPath)
    val preferences = read[SettingsContent](ujson.read(prefPath))
    preferences
  }

  def save(settingsContent: SettingsContent): Unit = {
    if (!os.exists(customSettingsPath))
      customSettingsPath.toIO.getParentFile.mkdirs()

    val settingsJson = write(settingsContent)
    val writer = new BufferedWriter(new FileWriter(customSettingsPath.toIO))
    writer.write(settingsJson)
    writer.close()
  }
}

package com.melvic.chi.config

import com.melvic.chi.Config
import com.melvic.chi.config.SettingsContent.{EditorSettings, ScalaSettings}
import upickle.default._

import java.io.{BufferedWriter, FileWriter}

final case class SettingsContent(editor: EditorSettings, scala: ScalaSettings)

object SettingsContent {
  final case class ScalaSettings(pointFree: Boolean, simplifyMatch: Boolean, usePredef: Boolean)
  final case class EditorSettings(
      evalOnType: Boolean,
      showLineNumbers: Boolean,
      maxColumnWidth: Int,
      showOutputInfo: Boolean
  )

  implicit def scalaPrefs(implicit prefs: Preferences): ScalaSettings =
    prefs.content.scala

  implicit val scalaConfigRW: ReadWriter[ScalaSettings] = macroRW[ScalaSettings]
  implicit val editorConfigRW: ReadWriter[EditorSettings] = macroRW[EditorSettings]
  implicit val prefRW: ReadWriter[SettingsContent] = macroRW[SettingsContent]

  val dummy = SettingsContent(
    EditorSettings(evalOnType = true, showLineNumbers = true, maxColumnWidth = 80, showOutputInfo = true),
    ScalaSettings(pointFree = false, simplifyMatch = false, usePredef = false)
  )

  val customSettingsPath = os.pwd / Config.CustomSettingsPath
  val defaultSettingsPath = os.resource / Config.DefaultSettingsPath

  def load: SettingsContent = {
    val prefData =
      if (os.exists(customSettingsPath)) os.read(customSettingsPath)
      else os.read(defaultSettingsPath)
    loadFromPathString(prefData)
  }

  def loadFromPathString(data: String) =
    read[SettingsContent](ujson.read(data))

  def loadDefaults: SettingsContent =
    loadFromPathString(os.read(defaultSettingsPath))

  def save(settingsContent: SettingsContent): Unit = {
    if (!os.exists(customSettingsPath))
      customSettingsPath.toIO.getParentFile.mkdirs()

    val settingsJson = write(settingsContent)
    val writer = new BufferedWriter(new FileWriter(customSettingsPath.toIO))
    writer.write(settingsJson)
    writer.close()
  }
}

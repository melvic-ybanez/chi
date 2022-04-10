package com.melvic.chi.views.prefs

import com.melvic.chi.views.FontUtils

import java.awt.Dimension
import javax.swing.JTree
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeCellRenderer, TreeSelectionModel}

object SettingsTree {
  def fromPreferencesDialog(preferencesDialog: PreferencesDialog): JTree = {
    val top = new DefaultMutableTreeNode("All Settings")
    val tree = new JTree(top)
    tree.getSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION)

    val Editor = "Editor"
    val Scala = "Scala"

    createTreeNode(top, Editor)
    createTreeNode(top, Scala)

    val renderer = tree.getCellRenderer.asInstanceOf[DefaultTreeCellRenderer]
    renderer.setLeafIcon(null)
    renderer.setClosedIcon(null)
    renderer.setOpenIcon(null)

    FontUtils.withComponentFont(tree)

    (0 until tree.getRowCount).foreach(r => tree.expandRow(r))

    tree.addTreeSelectionListener { event =>
      val node = tree.getLastSelectedPathComponent.asInstanceOf[DefaultMutableTreeNode]

      if (node == null) ()
      else node.getUserObject.asInstanceOf[String] match {
        case `Scala`  => preferencesDialog.showScalaSettings()
        case `Editor` => preferencesDialog.showEditorSettings()
        case _ => ()
      }
    }

    val path = tree.getPathForRow(1)
    tree.setSelectionPath(path)
    tree.scrollPathToVisible(path)

    tree
  }

  def createTreeNode(parent: DefaultMutableTreeNode, text: String): DefaultMutableTreeNode = {
    val node = new DefaultMutableTreeNode(text)
    parent.add(node)
    node
  }
}

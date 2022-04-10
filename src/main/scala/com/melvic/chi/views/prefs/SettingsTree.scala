package com.melvic.chi.views.prefs

import java.awt.Dimension
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeCellRenderer, TreePath, TreeSelectionModel}
import javax.swing.{JPanel, JTree}

object SettingsTree {
  def fromPreferencesDialog(preferencesDialog: PreferencesDialog): JTree = {
    val top = new DefaultMutableTreeNode("All Settings")
    val tree = new JTree(top)
    tree.getSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION)

    val editorNode = new DefaultMutableTreeNode("Editor")
    top.add(editorNode)

    val scalaNode = new DefaultMutableTreeNode("Scala")
    top.add(scalaNode)

    val renderer = tree.getCellRenderer.asInstanceOf[DefaultTreeCellRenderer]
    renderer.setLeafIcon(null)
    renderer.setClosedIcon(null)
    renderer.setOpenIcon(null)

    val path = tree.getPathForRow(0)
    tree.setSelectionPath(path)
    tree.scrollPathToVisible(path)

    (0 until tree.getRowCount).foreach(r => tree.expandRow(r))
    tree
  }
}

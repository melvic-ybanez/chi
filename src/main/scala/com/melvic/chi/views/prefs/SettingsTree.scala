package com.melvic.chi.views.prefs

import javax.swing.tree.{DefaultMutableTreeNode, TreeSelectionModel}
import javax.swing.{JPanel, JTree}

object SettingsTree {
  def fromNodeContents(scalaPane: JPanel): JTree = {
    val top = new DefaultMutableTreeNode("Preferences")
    val tree = new JTree(top)
    tree.getSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION)
    tree.setRootVisible(false)
    tree
  }
}

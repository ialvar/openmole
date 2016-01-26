package org.openmole.gui.client.core.files

/*
 * Copyright (C) 24/07/15 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.openmole.gui.client.core.AlertPanel
import org.openmole.gui.ext.data.SafePath
import rx._

package object treenodemanager {
  val instance = new TreeNodeManager

  def apply = instance
}

class TreeNodeManager {
  val root = DirNode(Var("projects"), Var(SafePath.sp(Seq("projects"))), 0L, "")

  val dirNodeLine: Var[Seq[DirNode]] = Var(Seq(root))

  val error: Var[Option[TreeNodeError]] = Var(None)

  val selectionMode: Var[Boolean] = Var(false)

  val selected: Var[Seq[TreeNode]] = Var(Seq())

  val copied: Var[Seq[TreeNode]] = Var(Seq())

  Obs(error) {
    error().map(AlertPanel.treeNodeDiv)
  }

  def isSelected(tn: TreeNode) = selected().contains(tn)

  def resetSelection = selected() = Seq()

  def setSelected(tn: TreeNode, b: Boolean) = b match {
    case true  ⇒ selected() = (selected() :+ tn).distinct
    case false ⇒ selected() = selected().filterNot(_ == tn)
  }

  def setSelectedAsCopied = {
    copied() = selected()
    selectionMode() = false
  }

  def emptyCopied = copied() = Seq()

  def setSelection = selectionMode() = true

  def setFilesInError(question: String, files: Seq[SafePath], okaction: () ⇒ Unit, cancelaction: () ⇒ Unit) = error() = Some(TreeNodeError(question, files, okaction, cancelaction))

  def noError = error() = None

  def switchOffSelection = {
    selectionMode() = false
    resetSelection
  }

  def head = dirNodeLine().head

  def current = dirNodeLine().last

  def take(n: Int) = dirNodeLine().take(n)

  def drop(n: Int) = dirNodeLine().drop(n)

  def +(dn: DirNode) = dirNodeLine() = dirNodeLine() :+ dn

  def switch(dn: DirNode) = {
    dirNodeLine() = dirNodeLine().zipWithIndex.filter(_._1 == dn).headOption.map {
      case (dn, index) ⇒ take(index + 1)
    }.getOrElse(dirNodeLine())
  }

  def allNodes = dirNodeLine().flatMap {
    _.sons()
  }

  private def selection = selectionMode() match {
    case true ⇒ Some(false)
    case _    ⇒ None
  }
}

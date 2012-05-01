/*
 * Copyright (C) 2011 <mathieu.leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.ide.plugin.hook.display

import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.mole.ICapsule
import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.core.model.control.IExecutionManager
import org.openmole.ide.core.model.panel.IHookPanelUI
import org.openmole.ide.misc.widget.multirow.RowWidget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos
import org.openmole.ide.misc.widget.multirow.MultiTwoCombos._
import java.awt.Font
import java.awt.Font._
import org.openmole.ide.misc.widget.MyPanel
import org.openmole.ide.misc.widget.PluginPanel
import scala.swing.MyComboBox
import scala.swing.event.SelectionChanged

object ToStringHookPanelUI{
  def rowFactory(hookpanel: ToStringHookPanelUI) = new Factory[IPrototype[_],ICapsule] {
    override def apply(row: TwoCombosRowWidget[IPrototype[_],ICapsule], p: MyPanel) = {
      import row._
      val twocomborow: TwoCombosRowWidget[IPrototype[_],ICapsule] = 
        new TwoCombosRowWidget(comboContentA,selectedA,comboContentB,selectedB,inBetweenString,plus) {
          override def doOnClose = hookpanel.executionManager.commitHook("org.openmole.plugin.hook.display.ToStringHook")
        }
      
      twocomborow.panel.listenTo(twocomborow.`combo1`,twocomborow.`combo2`)
      
      twocomborow.`combo1`.selection.reactions += {
        case SelectionChanged(twocomborow.`combo1`)=> 
          commit
        case _ =>
          }
          
          twocomborow.`combo2`.selection.reactions += {
        case SelectionChanged(twocomborow.`combo2`)=> 
          twocomborow.combo1.peer.setModel(MyComboBox.newConstantModel(hookpanel.protosFromTask(twocomborow.`combo2`.selection.item)))
          commit
        case _ =>
          }
      
      def commit = 
        hookpanel.executionManager.commitHook("org.openmole.plugin.hook.display.ToStringHook")
      
      twocomborow
    }
  }
}
import ToStringHookPanelUI._
class ToStringHookPanelUI(val executionManager: IExecutionManager) extends PluginPanel("wrap") with IHookPanelUI{

  val capsules = executionManager.capsuleMapping.values.filter(!_.outputs.isEmpty).toList
  
  val multiRow = {
    
    if (!capsules.isEmpty) {
      println("build hook row widg")
      val r =  new TwoCombosRowWidget(
        protosFromTask(capsules(0)), protosFromTask(capsules(0))(0),
        capsules,
        capsules(0),
        "from ",
        NO_ADD)
      
      val multiRow = new MultiTwoCombos("Displaying prototypes",
                                        List(r),
                                        rowFactory(this),
                                        CLOSE_IF_EMPTY,
                                        NO_ADD,
                                        true)
      
      contents += multiRow.panel
      Some(multiRow)
    } else {
      StatusBar.inform("No capsules or no prototypes are defined")
      None
    }
  }

  def protosFromTask(c: ICapsule): List[IPrototype[_]] =  c.outputs.map{_.prototype}.toList
  
  def saveContent = multiRow match {
    case Some(multiRow) => 
      multiRow.content.map{
        case(capsule, proto) => 
          new ToStringHookDataUI(executionManager,(proto, capsule))
      }
    case None => List()
  }
    
  def addHook = multiRow match {
    case Some(multiRow) => multiRow.addRow
    case None =>
  }
}
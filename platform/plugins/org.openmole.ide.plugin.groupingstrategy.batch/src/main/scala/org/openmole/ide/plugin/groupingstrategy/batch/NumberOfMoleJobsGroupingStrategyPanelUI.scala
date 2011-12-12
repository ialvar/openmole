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

package org.openmole.ide.plugin.groupingstrategy.batch

import org.openmole.core.model.mole.ICapsule
import org.openmole.ide.core.model.control.IExecutionManager
import org.openmole.ide.misc.widget.multirow.RowWidget._
import org.openmole.ide.misc.widget.multirow.MultiWidget._
import org.openmole.ide.misc.widget.multirow.MultiComboTextField
import org.openmole.ide.misc.widget.multirow.MultiComboTextField._
import java.awt.Font
import java.awt.Font._
import org.openmole.ide.core.model.panel.IGroupingStrategyPanelUI
import org.openmole.ide.misc.exception.GUIUserBadDataError
import org.openmole.ide.misc.widget.MigPanel
import scala.swing.Panel

object NumberOfMoleJobsGroupingStrategyPanelUI {
  def rowFactory(strategypanel: NumberOfMoleJobsGroupingStrategyPanelUI) = new Factory[ICapsule] {
    override def apply(row: ComboTextFieldRowWidget[ICapsule], p: Panel) = {
      import row._
     
      val combotfrow: ComboTextFieldRowWidget[ICapsule] = 
        new ComboTextFieldRowWidget(comboContentA,selectedA,"",plus)
      combotfrow
    }
  }
}

import NumberOfMoleJobsGroupingStrategyPanelUI._
//
class NumberOfMoleJobsGroupingStrategyPanelUI(val executionManager: IExecutionManager) extends MigPanel("") with IGroupingStrategyPanelUI{
  var multiRow : Option[MultiComboTextField[ICapsule]] = None
  val capsules : List[ICapsule]= executionManager.capsuleMapping.values.toList
  
  if (capsules.size>0){
    val r =  new ComboTextFieldRowWidget(capsules,
                                         capsules(0),
                                         "by",
                                         NO_ADD)
    
    multiRow =  Some(new MultiComboTextField("Group",
                                             List(r),
                                             rowFactory(this),
                                             CLOSE_IF_EMPTY,
                                             NO_ADD))
  }
  
  if (multiRow.isDefined) contents+= multiRow.get.panel
  
  def saveContent = {
    if (multiRow.isDefined) multiRow.get.content.map{c=> try{
        new NumberOfMoleJobsGroupingStrategyDataUI(executionManager,(c._1,c._2.toInt))
      } catch {
        case e:NumberFormatException=> throw new GUIUserBadDataError(c._2 + " is not an integer")
      }
    }
    else List()
  }
  
  def addStrategy = if (multiRow.isDefined) multiRow.get.showComponent
}
/*
 * Copyright (C) 2011 Mathieu leclaire <mathieu.leclaire at openmole.org>
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

package org.openmole.ide.core.serializer

import com.thoughtworks.xstream.XStream
import java.io.EOFException
import com.thoughtworks.xstream.io.xml.DomDriver
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import org.openmole.ide.core.exception.GUIUserBadDataError
import org.openmole.ide.core.control.MoleScenesManager
import org.openmole.ide.core.workflow.model.IEntityUI
import org.openmole.ide.core.palette.ElementFactories
import org.openmole.ide.core.palette.PaletteElementFactory
import org.openmole.ide.core.workflow.implementation.EntityUI
import org.openmole.ide.core.workflow.implementation.MoleScene
import org.openmole.ide.core.workflow.implementation.TaskUI
import org.openmole.ide.core.MoleSceneTopComponent
import org.openmole.ide.core.commons.Constants

object GUISerializer {
    
  val xstream = new XStream(new DomDriver)
  xstream.registerConverter(new MoleSceneConverter)
  
  xstream.alias("molescene", classOf[MoleScene])
  xstream.alias("entity", classOf[EntityUI])
  xstream.alias("task", classOf[TaskUI])
  
  def serialize(toFile: String) = {
    val writer = new FileWriter(new File(toFile))
    
    //root node
    val out = xstream.createObjectOutputStream(writer, "openmole")

    //prototypes
    ElementFactories.getAll(Constants.PROTOTYPE).foreach(pef=> out.writeObject(pef.entity))

    //tasks
    ElementFactories.getAll(Constants.TASK).foreach(pef=> out.writeObject(pef.entity))
        
    //samplings
    ElementFactories.getAll(Constants.SAMPLING).foreach(pef=> out.writeObject(pef.entity))
    
    //factories
    out.writeObject(ElementFactories.factories.toMap)

    //molescenes
    MoleScenesManager.moleScenes.foreach(out.writeObject(_))
    
    out.close
  }
  
  def unserialize(fromFile: String) = {
    val reader = new FileReader(new File(fromFile))
    val in = xstream.createObjectInputStream(reader)
   
    ElementFactories.clearAll
    MoleScenesManager.removeMoleScenes
    
    try {
      while(true) {
        val readObject = in.readObject
        readObject match{
          case x: IEntityUI=> new PaletteElementFactory(x.panelUIData.name,x,ElementFactories.factories(x))
          case x: MoleScene=> MoleScenesManager.addMoleScene(x)
          case _=> throw new GUIUserBadDataError("Failed to unserialize object " + readObject.toString)
        }
      }
    } catch {
      case eof: EOFException => println("Ugly stop condition of Xstream reader !")
    } finally {
      MoleSceneTopComponent.getDefault().refreshPalette();
      in.close
    }
  }
}
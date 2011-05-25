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

import org.openmole.ide.core.palette.ElementFactories
import org.openmole.ide.core.workflow.implementation.MoleSceneManager
import scala.collection.JavaConversions._

object MoleMaker {
  
  def buildMole(manager: MoleSceneManager){
    manager.capsuleViews.values.foreach(cv =>{
        val tui = cv.capsuleModel.taskUI.get
        ElementFactories.factories(tui).coreObject(tui.panelUIData)
      })
  }


}
//package org.openmole.ide.core.serializer;
//
//import org.openmole.commons.exception.UserBadDataError;
//import org.openmole.core.implementation.mole.Mole;
//import org.openmole.core.implementation.transition.Slot;
//import org.openmole.core.model.capsule.IGenericCapsule;
//import org.openmole.core.model.mole.IMole;
//import org.openmole.ide.core.control.MoleScenesManager;
//import org.openmole.ide.core.workflow.implementation.CapsuleModelUI;
//import org.openmole.ide.core.workflow.implementation.MoleSceneManager;
//import org.openmole.ide.core.workflow.model.ICapsuleModelUI;
//import org.openmole.ide.core.workflow.model.IMoleScene;
//
///**
// *
// * @author Mathieu Leclaire <mathieu.leclaire@openmole.org>
// */
//public class MoleMaker {
//
////    public static IMole processFromMoleScene(IMoleScene scene) throws UserBadDataError {
////        MoleSceneManager manager = scene.getManager();
////
////        //First capsule
////        ICapsuleModelUI start = manager.getStartingCapsule();
////        if (start == CapsuleModelUI.EMPTY_CAPSULE_MODEL) {
////            throw new UserBadDataError("A starting capsule is expected");
////        }
////
////        IMole mole = new Mole(exploreCapsuleModel(start));
////        return mole;
////    }
////
////    public static IGenericCapsule exploreCapsuleModel(ICapsuleModelUI capsuleModel) throws UserBadDataError {
////        IGenericCapsule capsule = CoreClassInstanciator.instanciateCapsule(capsuleModel);
////        while (capsule.intputSlots().size() < capsuleModel.getNbInputslots()) {
////      //      capsule.addInputSlot(new Slot(capsule));
////        }
//////        if (capsuleModel.hasChild()) {
//////            for (Iterator<ICapsuleModelUI> child = capsuleModel.getChilds().iterator(); child.hasNext();) {
//////                new Transition((ICapsule) capsule, exploreCapsuleModel(child.next()));
//////            }
//////        }
////        return capsule;
////    }
//
//    public static IMoleScene processToMoleScene(IMole mole) {
//
//        IMoleScene moleS = MoleScenesManager.getInstance().addMoleScene();
//
//        return moleS;
//    }
//}
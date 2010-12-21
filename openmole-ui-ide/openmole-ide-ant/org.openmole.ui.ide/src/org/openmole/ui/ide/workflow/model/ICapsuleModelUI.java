/*
 *  Copyright (C) 2010 leclaire
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Affero GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.ui.ide.workflow.model;

import org.openmole.ui.ide.commons.IOType;
import org.openmole.core.model.capsule.IGenericCapsule;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.fr>
 */
public interface ICapsuleModelUI<T extends IGenericCapsule> extends IObjectModelUI<T>{
    //IGenericTaskCapsule getTaskCapsule();
 //   void setTaskCapsule(IGenericTaskCapsule taskCapsule);
  //  void setTransitionTo(IGenericTaskCapsule tc);
    void addTransition(ICapsuleModelUI tModel);
    void addOutputSlot();
    void addInputSlot();
    int getNbInputslots();
    int getNbOutputslots();
    boolean isSlotRemovable(IOType type);
    boolean isSlotAddable(IOType type);
    void removeSlot(IOType type);
}

/*
 * Copyright (C) 2010 reuillon
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.implementation.transition

import org.openmole.commons.aspect.eventdispatcher.IObjectListenerWithArgs
import org.openmole.commons.exception.{InternalProcessingError, UserBadDataError}
import org.openmole.commons.tools.service.Priority
import org.openmole.core.implementation.internal.Activator
import org.openmole.core.implementation.data.Context
import org.openmole.core.implementation.tools.ContextAggregator
import org.openmole.core.implementation.tools.ContextBuffer
import org.openmole.core.implementation.tools.ToCloneFinder
import org.openmole.core.model.capsule.ICapsule
import org.openmole.core.model.capsule.IGenericCapsule
import org.openmole.core.model.data.IContext
import org.openmole.core.model.job.IMoleJob
import org.openmole.core.model.job.ITicket
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.mole.ISubMoleExecution
import org.openmole.core.model.transition.IAggregationTransition
import org.openmole.core.model.transition.ICondition
import org.openmole.core.model.transition.ISlot
import scala.collection.immutable.TreeSet
import scala.collection.mutable.ListBuffer

class AggregationTransition(start: ICapsule, end: ISlot, condition: ICondition, filtered: Set[String]) extends Transition(start, end, condition, filtered) with IAggregationTransition {

  class AggregationTransitionAdapter extends IObjectListenerWithArgs[ISubMoleExecution] {

    override def eventOccured(subMole: ISubMoleExecution, args: Array[Object]) = {
      if (!classOf[IMoleJob].isAssignableFrom(args(0).getClass)) {
        throw new InternalProcessingError("BUG: argument of the event has the wrong type.");
      }

      val lastJob = args(0).asInstanceOf[IMoleJob]
      val moleExecution = args(1).asInstanceOf[IMoleExecution]
      val ticket = args(2).asInstanceOf[ITicket];
      subMoleFinished(subMole, lastJob, ticket, moleExecution)
    }
  }


  def this(start: ICapsule, end: IGenericCapsule) = this(start, end.defaultInputSlot, ICondition.True, Set.empty[String])
    
  def this(start: ICapsule, end: IGenericCapsule, condition: ICondition) = this(start, end.defaultInputSlot, condition, Set.empty[String])

  def this(start: ICapsule, end: IGenericCapsule, condition: String) = this(start, end.defaultInputSlot, new Condition(condition), Set.empty[String])
    
  def this(start: ICapsule , slot: ISlot, condition: String) = this(start, slot, new Condition(condition), Set.empty[String])
    
  def this(start: ICapsule , slot: ISlot, condition: ICondition) = this(start, slot, condition, Set.empty[String])
   
  def this(start: ICapsule, end: IGenericCapsule, filtred: Array[String]) = this(start, end.defaultInputSlot, ICondition.True, filtred.toSet)
    
  def this(start: ICapsule, end: IGenericCapsule, condition: ICondition, filtred: Array[String]) = this(start, end.defaultInputSlot, condition, filtred.toSet)

  def this(start: ICapsule, end: IGenericCapsule, condition: String, filtred: Array[String]) = this(start, end.defaultInputSlot, new Condition(condition), filtred.toSet)
    
  def this(start: ICapsule , slot: ISlot, condition: String, filtred: Array[String]) = this(start, slot, new Condition(condition), filtred.toSet)

  override def performImpl(global: IContext, context: IContext, ticket: ITicket, toClone: Set[String], moleExecution: IMoleExecution, subMole: ISubMoleExecution) = synchronized {

    val registry = moleExecution.localCommunication.aggregationTransitionRegistry

    val parent = ticket.parent match {
      case None => throw new UserBadDataError("Aggregation transition should take place after an exploration")
      case Some(p) => p
    }
    
    val resultContexts = registry.consult(this, parent) match {
      case None => 
        val res = new ContextBuffer(true)
        registry.register(this, parent, res)
        Activator.getEventDispatcher.registerForObjectChangedSynchronous(subMole, Priority.LOW, new AggregationTransitionAdapter, ISubMoleExecution.Finished)
        res
      case Some(res) => res
    }
 
    //Store the result context
    resultContexts ++= (context, toClone)
  }

  def subMoleFinished(subMole: ISubMoleExecution, job: IMoleJob, ticket: ITicket, moleExecution: IMoleExecution) = {
    def registry =  moleExecution.localCommunication.aggregationTransitionRegistry

    val newTicket = ticket.parent match {
      case None => throw new UserBadDataError("Aggregation transition should take place after an exploration")
      case Some(p) => p
    }
    
    val result = registry.remove(this, newTicket) match {
      case None => throw new InternalProcessingError("No context registred for the aggregation transition")
      case Some(res) => res
    }
    
    val endTask = end.capsule.task match {
      case None => throw new UserBadDataError("No task assigned for end capsule")
      case Some(t) => t 
    }
      
    val startTask = start.task match {
      case None => throw new UserBadDataError("No task assigned for start capsule")
      case Some(t) => t 
    }  
    
   /* val dataToAggregate = ContextAggregator.dataIn1WhichAreAlsoIn2(endTask.inputs, startTask.outputs)
    
    val newContextEnd = ContextAggregator.aggregate(dataToAggregate, true, result._1)*/

    val subMoleParent = subMole.parent match {
      case None => throw new InternalProcessingError("Submole execution has no parent")
      case Some(p) => p
    }
    
    //Variable have are clonned in other transitions if necessary
    submitNextJobsIfReady(job.globalContext, result, newTicket, moleExecution, subMoleParent)
  }
}

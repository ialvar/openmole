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

package org.openmole.ide.core.implementation.serializer

import org.openmole.core.model.data.IPrototype
import org.openmole.core.model.execution.IEnvironment
import org.openmole.core.model.mole.ICapsule
import org.openmole.ide.core.implementation.dialog.StatusBar
import org.openmole.ide.core.implementation.registry.PrototypeKey
import org.openmole.ide.core.implementation.registry.KeyPrototypeGenerator
import org.openmole.ide.core.model.commons.TransitionType._
import org.openmole.core.model.mole.IEnvironmentSelection
import org.openmole.core.model.mole.IGrouping
import org.openmole.core.model.mole.IMole
import org.openmole.ide.core.model.data.ICapsuleDataUI
import org.openmole.ide.core.model.data.IMoleDataUI
import org.openmole.ide.core.model.dataproxy._
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.core.implementation.task._
import java.io.File
import org.openmole.core.implementation.data.DataChannel
import org.openmole.core.implementation.data.DataSet
import org.openmole.core.implementation.data.Parameter
import org.openmole.core.implementation.data.ParameterSet
import org.openmole.core.implementation.mole._
import org.openmole.core.implementation.transition._
import org.openmole.ide.misc.tools.check.TypeCheck
import org.openmole.misc.exception.UserBadDataError
import org.openmole.ide.core.model.workflow.IDataChannelUI
import org.openmole.ide.core.model.workflow.IMoleSceneManager
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.core.model.task.ITask
import org.openmole.core.model.transition.ICondition
import org.openmole.ide.core.implementation.data.EmptyDataUIs
import org.openmole.ide.core.implementation.dataproxy.Proxys
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.workflow.ITransitionUI
import scala.collection.JavaConversions._
import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer

object MoleMaker {

  def buildMoleExecution(mole: IMole,
                         manager: IMoleSceneManager,
                         capsuleMap: Map[ICapsuleUI, ICapsule],
                         groupingStrategies: List[(IGrouping, ICapsule)]): (IMoleExecution, Iterable[(IEnvironment, String)]) =
    try {
      var envs = new HashSet[(IEnvironment, String)]
      val strat = new ListBuffer[(ICapsule, IEnvironmentSelection)]

      manager.capsules.values.foreach { c ⇒
        c.dataUI.environment match {
          case Some(x: IEnvironmentDataProxyUI) ⇒
            try {
              val env = x.dataUI.coreObject
              envs += env -> x.dataUI.name
              strat += capsuleMap(c) -> new FixedEnvironmentSelection(env)
            } catch {
              case e: UserBadDataError ⇒ StatusBar.block(e.message, stack = e.getStackTraceString)
            }
          case _ ⇒
        }
      }

      val grouping = groupingStrategies.map { case (s, c) ⇒ c -> s }.toMap

      (new MoleExecution(mole, strat.toMap, grouping), envs.toSet)
    }

  def buildMole(manager: IMoleSceneManager): Either[String, (IMole, Map[ICapsuleUI, ICapsule], Map[IPrototypeDataProxyUI, IPrototype[_]], Iterable[(ICapsuleUI, Throwable)])] = {
    try {
      if (manager.startingCapsule.isDefined) {
        val prototypeMap: Map[IPrototypeDataProxyUI, IPrototype[_]] = Proxys.prototypes.map { p ⇒ p -> p.dataUI.coreObject }.toMap
        val builds = manager.capsules.map { c ⇒
          (c._2 -> buildCapsule(c._2.dataUI, manager.dataUI), None)
        }.toMap

        val capsuleMap: Map[ICapsuleUI, ICapsule] = builds.map { case ((cui, c), _) ⇒ cui -> c }
        val errors = builds.flatMap { case ((_, _), e) ⇒ e }

        capsuleMap.foreach {
          case (cui, ccore) ⇒
            manager.capsuleConnections(cui.dataUI).foreach { t ⇒
              t match {
                case x: ITransitionUI ⇒ buildTransition(ccore,
                  capsuleMap(x.target.capsule),
                  x, prototypeMap)

                case x: IDataChannelUI ⇒ new DataChannel(capsuleMap(x.source),
                  capsuleMap(x.target.capsule),
                  x.filteredPrototypes.map { p ⇒ prototypeMap(p).name }.toSeq: _*)
              }
            }
        }
        Right((new Mole(capsuleMap(manager.startingCapsule.get)), capsuleMap, prototypeMap, errors))
      } else throw new UserBadDataError("No starting capsule is defined. The mole construction is not possible. Please define a capsule as a starting capsule.")
    } catch {
      case e ⇒
        Left(e.getMessage)
    }
  }

  def prototypeMapping: Map[IPrototypeDataProxyUI, IPrototype[_]] = (Proxys.prototypes.toList :::
    List(EmptyDataUIs.emptyPrototypeProxy)).map { p ⇒ p -> p.dataUI.coreObject }.toMap

  def keyPrototypeMapping: Map[PrototypeKey, IPrototypeDataProxyUI] = (Proxys.prototypes.toList :::
    List(EmptyDataUIs.emptyPrototypeProxy)).map { p ⇒ KeyPrototypeGenerator(p) -> p }.toMap

  def buildCapsule(proxy: ITaskDataProxyUI,
                   plugins: Set[File] = Set.empty) =
    taskCoreObject(proxy, plugins) match {
      case Right(x: ITask) ⇒ new Capsule(x)
      case Left(x: Throwable) ⇒ new Capsule
    }

  def buildCapsule(capsuleDataUI: ICapsuleDataUI,
                   moleDataUI: IMoleDataUI): ICapsule =
    capsuleDataUI.task match {
      case Some(x: ITaskDataProxyUI) ⇒
        buildCapsule(x, moleDataUI.plugins.map { p ⇒ new File(p) }.toSet)
      case _ ⇒
        StatusBar.inform("A capsule without Task can not be run")
        new Capsule

    }

  def taskCoreObject(proxy: ITaskDataProxyUI,
                     plugins: Set[File] = Set.empty): Either[Throwable, ITask] =
    try {
      Right(proxy.dataUI.coreObject(inputs(proxy),
        outputs(proxy),
        parameters(proxy),
        new PluginSet(plugins)))
    } catch {
      case e ⇒
        StatusBar.warn(e.getMessage, Some(proxy), e.getStackTraceString)
        Left(e)
    }

  def taskCoreObject(capsuleDataUI: ICapsuleDataUI,
                     plugins: Set[File]): Either[Throwable, ITask] =
    taskCoreObject(capsuleDataUI.task.get, plugins)

  def inputs(proxy: ITaskDataProxyUI) = DataSet(proxy.dataUI.prototypesIn.map { _.dataUI.coreObject })
  def outputs(proxy: ITaskDataProxyUI) = DataSet(proxy.dataUI.prototypesOut.map { _.dataUI.coreObject })

  def parameters(proxy: ITaskDataProxyUI) =
    new ParameterSet(proxy.dataUI.inputParameters.flatMap {
      case (protoProxy, v) ⇒
        if (!v.isEmpty) {
          val proto = protoProxy.dataUI.coreObject
          val (msg, obj) = TypeCheck(v, proto)
          obj match {
            case Some(x: Object) ⇒ Some(new Parameter(proto.asInstanceOf[IPrototype[Any]], x))
            case _ ⇒ None
          }
        } else None
    }.toList)

  def inputs(capsuleDataUI: ICapsuleDataUI): DataSet = inputs(capsuleDataUI.task.get)

  def outputs(capsuleDataUI: ICapsuleDataUI): DataSet = outputs(capsuleDataUI.task.get)

  def parameters(capsuleDataUI: ICapsuleDataUI): ParameterSet = parameters(capsuleDataUI.task.get)

  def buildTransition(sourceCapsule: ICapsule,
                      targetCapsule: ICapsule,
                      t: ITransitionUI,
                      prototypeMap: Map[IPrototypeDataProxyUI, IPrototype[_]]) {
    val filtered = t.filteredPrototypes.map { p ⇒ prototypeMap(p).name }
    val condition = if (t.condition.isDefined) new Condition(t.condition.get) else ICondition.True
    t.transitionType match {
      case BASIC_TRANSITION ⇒ new Transition(sourceCapsule, targetCapsule, condition, filtered)
      case AGGREGATION_TRANSITION ⇒ new AggregationTransition(sourceCapsule, targetCapsule, condition, filtered)
      case EXPLORATION_TRANSITION ⇒ new ExplorationTransition(sourceCapsule, targetCapsule, condition, filtered)
      case _ ⇒ throw new UserBadDataError("No matching type between capsule " + sourceCapsule + " and " + targetCapsule + ". The transition can not be built")
    }
  }
}

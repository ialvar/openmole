/*
 * Copyright (C) 2012 mathieu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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

package org.openmole.ide.core.implementation.workflow

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants._
import org.openide.DialogDescriptor
import org.openide.DialogDisplayer
import org.openmole.core.model.mole.IMoleExecution
import org.openmole.ide.core.implementation.execution.ExecutionManager
import org.openmole.ide.core.implementation.execution.MoleFinishedEvent
import org.openmole.ide.core.implementation.serializer.MoleMaker
import org.openmole.ide.core.model.dataproxy.ITaskDataProxyUI
import org.openmole.ide.core.model.panel.IHookPanelUI
import org.openmole.ide.core.model.workflow.ICapsuleUI
import org.openmole.ide.core.model.workflow.ISceneContainer
import org.openmole.ide.misc.widget.MainLinkLabel
import org.openmole.ide.misc.widget.PluginPanel
import scala.collection.mutable.HashMap
import scala.swing.Action
import scala.swing.Button
import scala.swing.Label
import scala.swing.Orientation
import scala.swing.Panel
import scala.swing.Separator
import scala.swing.TabbedPane
import org.openmole.ide.misc.tools.image.Images._

class ExecutionMoleSceneContainer(val scene: ExecutionMoleScene,
                                  val page: TabbedPane.Page,
                                  bmsc: BuildMoleSceneContainer) extends Panel with ISceneContainer {
  peer.setLayout(new BorderLayout)

  val executionManager =
    MoleMaker.buildMole(bmsc.scene.manager) match {
      case Right((mole, prototypeMapping, capsuleMapping, errors)) ⇒
        Some(new ExecutionManager(bmsc.scene.manager,
          mole,
          prototypeMapping,
          capsuleMapping))
      case Left(l) ⇒
    }

  val startStopButton = new Button(start) {
    background = new Color(125, 160, 0)
  }

  var panelHooks = new HashMap[IHookPanelUI, ICapsuleUI]
  executionManager match {
    case Some(x: ExecutionManager) ⇒

      peer.add(new ExecutionPanel {
        peer.setLayout(new BorderLayout)
        peer.add(new PluginPanel("wrap") {
          val hookTabbedPane = new TabbedPane

          //Hooks
          contents += new Label { text = "<html><b><font \"size=\"5\" >Hooks</font></b></html>" }
          contents += hookTabbedPane
          x.capsuleMapping.keys.foreach { c ⇒
            c.dataUI.task match {
              case Some(t: ITaskDataProxyUI) ⇒
                val activated = c.dataUI.hooks.filter { _._2.activated }
                if (!activated.isEmpty) {
                  hookTabbedPane.pages += new TabbedPane.Page("<html><b>" + t.dataUI.name + "</b></html>",
                    new PluginPanel("", "", "[top]") {
                      activated.values.foreach { h ⇒
                        val p = h.buildPanelUI(t)
                        panelHooks += p -> c
                        contents += p.peer
                        contents += new Separator(Orientation.Vertical) { foreground = Color.WHITE }
                      }
                    })
                }
              case _ ⇒
            }
          }
          contents += new Label { text = "<html><b><font \"size=\"5\" >Grouping strategy</font></b></html>" }
        }.peer, BorderLayout.CENTER)

        peer.add(new PluginPanel("wrap") {
          contents += new Label { text = "<html><b><font \"size=\"5\" >Execution control</font></b></html>" }
          contents += new PluginPanel("wrap 2", "[]30[]") {
            //Start / Stop
            contents += startStopButton

            // View Mole execution
            contents += new MainLinkLabel("Mole execution", new Action("") {
              def apply =
                DialogDisplayer.getDefault.notify(new DialogDescriptor(new JScrollPane(scene.graphScene.createView) {
                  setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
                }, "Mole execution"))
            })
          }
        }.peer, BorderLayout.SOUTH)
      }.peer, BorderLayout.CENTER)

      // Execution
      peer.add(new PluginPanel("wrap", "[grow]") {
        add(x, "growx")
      }.peer, BorderLayout.SOUTH)
    case None ⇒
  }

  def start: Action = new Action("Start") {
    override def apply = executionManager match {
      case Some(x: ExecutionManager) ⇒
        listenTo(x)
        reactions += {
          case MoleFinishedEvent ⇒
            startLook
        }
        startStopButton.background = new Color(170, 0, 0)
        startStopButton.action = stop
        x.start(panelHooks.toMap)
      case _ ⇒
    }
  }

  def startLook = {
    startStopButton.background = new Color(125, 160, 0)
    startStopButton.action = start
  }

  def stop: Action = new Action("Stop") {
    override def apply = executionManager match {
      case Some(x: ExecutionManager) ⇒
        startStopButton.background = new Color(125, 160, 0)
        startStopButton.action = start
        x.cancel
      case _ ⇒
    }
  }

  def started = executionManager match {
    case Some(x: ExecutionManager) ⇒
      x match {
        case me: IMoleExecution ⇒ me.started
        case _ ⇒ false
      }
    case None ⇒ false
  }

  def finished = executionManager match {
    case Some(x: ExecutionManager) ⇒
      x match {
        case me: IMoleExecution ⇒ me.finished
        case _ ⇒ true
      }
    case None ⇒ true
  }

  class ExecutionPanel extends Panel {
    background = new Color(77, 77, 77)
  }
}
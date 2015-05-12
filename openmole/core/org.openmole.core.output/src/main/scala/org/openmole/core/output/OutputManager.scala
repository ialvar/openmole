/*
 * Copyright (C) 2015 Romain Reuillon
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
package org.openmole.core.output

import java.io.{ PrintStream, OutputStream }

import scala.collection.mutable

object OutputManager {

  private lazy val output = mutable.WeakHashMap[ThreadGroup, PrintStream]()
  private lazy val error = mutable.WeakHashMap[ThreadGroup, PrintStream]()

  val systemOutput = System.out
  val systemError = System.err

  private def findRedirect(group: ThreadGroup, map: mutable.Map[ThreadGroup, PrintStream]): Option[PrintStream] = {
    def parentThreadGroups(group: ThreadGroup) =
      Iterator.iterate(group)(_.getParent).takeWhile(_ != null)

    parentThreadGroups(group).map(map.get).find(_.isDefined).map(_.get)
  }

  lazy val dispatchOutput = new OutputStream {
    override def write(i: Int): Unit = output.synchronized {
      findRedirect(Thread.currentThread().getThreadGroup, output) match {
        case None     ⇒ systemOutput.write(i)
        case Some(os) ⇒ os.write(i)
      }
    }
  }

  lazy val dispatchError = new OutputStream {
    override def write(i: Int): Unit = error.synchronized {
      findRedirect(Thread.currentThread().getThreadGroup, error) match {
        case None     ⇒ systemError.write(i)
        case Some(os) ⇒ os.write(i)
      }
    }
  }

  def redirectSystemOutput(ps: PrintStream) = {
    System.setOut(ps)
    Console.getClass.getMethod("setOutDirect", classOf[PrintStream]).invoke(Console, ps)
  }

  def redirectSystemError(ps: PrintStream) = {
    System.setErr(ps)
    Console.getClass.getMethod("setErrDirect", classOf[PrintStream]).invoke(Console, ps)
  }

  redirectSystemOutput(new PrintStream(dispatchOutput))
  redirectSystemError(new PrintStream(dispatchError))

  def unregister(thread: ThreadGroup) = {
    output.synchronized { output.remove(thread) }
    error.synchronized { error.remove(thread) }
  }

  def redirectOutput(thread: ThreadGroup, stream: PrintStream) = output.synchronized {
    output.put(thread, stream)
  }

  def redirectError(thread: ThreadGroup, stream: PrintStream) = error.synchronized {
    error.put(thread, stream)
  }

}
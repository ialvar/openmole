/*
 * Copyright (C) 2010 reuillon
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

package org.openmole.commons.tools.service

class MoovingAverage(period: Int) {
  private val queue = new scala.collection.mutable.Queue[Double]
  def apply(n: Double) = synchronized {
    queue.enqueue(n)
    if (queue.size > period)
      queue.dequeue
    queue.sum / queue.size
  }
  def get = synchronized (queue.sum / queue.size)
  override def toString = get.toString
  def clear = queue.clear
}

/*
 * Copyright (C) 17/02/13 Romain Reuillon
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

package org.openmole.core.implementation.tools

import org.openmole.core.model.data._

trait InputOutputBuilder { builder ⇒
  private var _inputs = DataSet.empty
  private var _outputs = DataSet.empty
  private var _parameters = ParameterSet.empty

  def addInput(d: DataSet): this.type = { _inputs ++= d; this }
  def addInput(d: Data[_]): this.type = { _inputs += d; this }

  def addOutput(d: DataSet): this.type = { _outputs ++= d; this }
  def addOutput(d: Data[_]): this.type = { _outputs += d; this }

  def setDefault[T](p: Prototype[T], v: T, `override`: Boolean = false): this.type = setDefault(Parameter(p, v, `override`))
  def setDefault(p: Parameter[_]): this.type = { _parameters += p; this }

  @deprecated("use setDefault instead", "4.0")
  def addParameter(p: Parameter[_]) = { _parameters += p; this }
  def addParameter(p: ParameterSet) = { _parameters ++= p; this }

  def inputs = _inputs
  def outputs = _outputs
  def parameters = _parameters

  trait Built {
    val inputs = builder.inputs
    val outputs = builder.outputs
    val parameters = builder.parameters
  }
}

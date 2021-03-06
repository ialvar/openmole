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

package org.openmole.core.workflow.tools

import org.openmole.core.context._
import org.openmole.core.exception._
import org.openmole.core.expansion.FromContext
import org.openmole.core.fileservice.FileService
import org.openmole.core.preference.Preference
import org.openmole.core.workspace.NewFile
import org.openmole.tool.random._

object InputOutputCheck {

  trait InputError

  case class InputNotFound(input: Val[_]) extends InputError {
    override def toString = s"Input data '$input' has not been found"
  }

  case class InputTypeMismatch(input: Val[_], found: Val[_]) extends InputError {
    override def toString = s"Input data named '$found' is of an incompatible with the required '$input'"
  }

  trait OutputError

  case class OutputNotFound(output: Val[_]) extends OutputError {
    override def toString = s"Output data '$output' has not been found"
  }

  case class OutputTypeMismatch(output: Val[_], variable: Variable[_]) extends OutputError {
    override def toString = s"""Type mismatch the content of the output value '${output.name}' of type '${variable.value.getClass}' is incompatible with the output variable '${output}'."""
  }

  protected def verifyInput(inputs: PrototypeSet, context: Context): Iterable[InputError] =
    (for {
      p ← inputs.toList
    } yield context.variable(p.name) match {
      case None    ⇒ Some(InputNotFound(p))
      case Some(v) ⇒ if (!p.isAssignableFrom(v.prototype)) Some(InputTypeMismatch(p, v.prototype)) else None
    }).flatten

  protected def verifyOutput(outputs: PrototypeSet, context: Context): Iterable[OutputError] =
    outputs.flatMap {
      d ⇒
        context.variable(d) match {
          case None ⇒ Some(OutputNotFound(d))
          case Some(v) ⇒
            if (!d.accepts(v.value))
              Some(OutputTypeMismatch(d, v))
            else None
        }
    }

  def filterOutput(outputs: PrototypeSet, context: Context): Context =
    Context(outputs.toList.flatMap(o ⇒ context.variable(o): Option[Variable[_]]): _*)

  def initializeInput(defaults: DefaultSet, context: Context)(implicit randomProvider: RandomProvider, newFile: NewFile, fileService: FileService): Context =
    context ++
      defaults.flatMap {
        parameter ⇒
          if (parameter.`override` || !context.contains(parameter.prototype.name)) Some(parameter.toVariable.from(context))
          else Option.empty[Variable[_]]
      }

  def perform(inputs: PrototypeSet, outputs: PrototypeSet, defaults: DefaultSet, process: FromContext[Context])(implicit preference: Preference) = FromContext.withValidation(process) { p ⇒
    import p._
    val initializedContext = initializeInput(defaults, context)
    val inputErrors = verifyInput(inputs, initializedContext)
    if (!inputErrors.isEmpty) throw new InternalProcessingError(s"Input errors have been found in ${this}: ${inputErrors.mkString(", ")}.")

    val result =
      try initializedContext + process.from(initializedContext)
      catch {
        case e: Throwable ⇒
          throw new InternalProcessingError(e, s"Error for context values in ${this} ${initializedContext.prettified}")
      }

    val outputErrors = verifyOutput(outputs, result)
    if (!outputErrors.isEmpty) throw new InternalProcessingError(s"Output errors in ${this}: ${outputErrors.mkString(", ")}.")
    filterOutput(outputs, result)
  }

}

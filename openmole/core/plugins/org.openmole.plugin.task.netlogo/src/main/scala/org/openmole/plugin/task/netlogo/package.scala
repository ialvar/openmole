/*
 * Copyright (C) 2014 Romain Reuillon
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

package org.openmole.plugin.task

import org.openmole.core.workflow.data.Prototype
import org.openmole.core.workflow.builder._
import org.openmole.misc.macros.Keyword._

package object netlogo {

  lazy val netLogoInputs =
    add[{
      def addNetLogoInput(p: Prototype[_], n: String)
      def addNetLogoInput(p: Prototype[_])
    }]

  lazy val netLogoOutput =
    add[{
      def addNetLogoOutput(n: String, p: Prototype[_])
      def addNetLogoOutput(p: Prototype[_])
      def addNetLogoOutput(name: String, column: Int, p: Prototype[_])
    }]

  trait NetLogoPackage extends external.ExternalPackage {
    lazy val netLogoInputs = netlogo.netLogoInputs
    lazy val netLogoOutput = netlogo.netLogoOutput
  }

}

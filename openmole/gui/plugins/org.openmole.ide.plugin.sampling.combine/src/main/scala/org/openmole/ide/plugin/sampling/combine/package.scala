/*
 * Copyright (C) 17/10/13 Romain Reuillon
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

package org.openmole.ide.plugin.sampling

import org.openmole.ide.core.implementation.data.SamplingDataUI
import org.openmole.ide.core.implementation.serializer.Update
import org.openmole.ide.core.implementation.dataproxy.PrototypeDataProxyUI
import org.openmole.core.model.sampling.{ Sampling, Factor }
import org.openmole.ide.plugin.sampling.modifier._

package object combine {

  class ShuffleSamplingDataUI extends Update[ShuffleSamplingDataUI2] {
    def update = new ShuffleSamplingDataUI2
  }

  class TakeSamplingDataUI(val size: String = "1") extends Update[TakeSamplingDataUI2] {
    def update = new TakeSamplingDataUI2(size)
  }

  class ZipSamplingDataUI extends Update[ZipSamplingDataUI2] {
    def update = new ZipSamplingDataUI2
  }

  class ZipWithIndexSamplingDataUI(val prototype: Option[PrototypeDataProxyUI] = None) extends Update[ZipWithIndexSamplingDataUI2] {
    def update = new ZipWithIndexSamplingDataUI2(prototype)
  }

  class ZipWithNameSamplingDataUI(val prototype: Option[PrototypeDataProxyUI] = None) extends Update[ZipWithNameSamplingDataUI2] {
    def update = new ZipWithNameSamplingDataUI2(prototype)
  }

}

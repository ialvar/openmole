/*
 * Copyright (C) 2012 reuillon
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

package org.openmole.plugin.environment.batch.refresh

import org.openmole.plugin.environment.batch.environment.{ BatchEnvironment, UsageControl }
import org.openmole.tool.logger.JavaLogger

object DeleteActor extends JavaLogger {
  def receive(msg: DeleteFile)(implicit services: BatchEnvironment.Services) = {
    import services._

    val DeleteFile(storage, path, directory) = msg
    try UsageControl.tryWithToken(storage.usageControl) {
      case Some(t) ⇒
        if (directory) storage.rmDir(path)(t) else storage.rmFile(path)(t)
      case None ⇒
        JobManager ! Delay(msg, BatchEnvironment.getTokenInterval)
    }
    catch {
      case t: Throwable ⇒
        Log.logger.log(Log.FINE, "Error when deleting a file", t)
    }
  }
}
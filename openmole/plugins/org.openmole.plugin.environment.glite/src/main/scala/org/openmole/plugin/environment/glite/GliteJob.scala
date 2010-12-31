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

package org.openmole.plugin.environment.glite

import org.openmole.commons.exception.InternalProcessingError
import org.openmole.plugin.environment.glite.internal.Activator._
import org.openmole.plugin.environment.jsaga.JSAGAJob
import org.openmole.commons.tools.service.RNG
import org.openmole.core.batch.environment.ShouldBeKilledException
import org.openmole.core.model.execution.ExecutionState
import fr.in2p3.jsaga.adaptor.job.SubState


class GliteJob(jobId: String, jobService: GliteJobService, proxyExpired: Long) extends JSAGAJob(jobId, jobService){

  var lastUpdate = System.currentTimeMillis
  
  override def updateState: ExecutionState = {
    val ret = super.updateState
    lastUpdate = System.currentTimeMillis
    
    if(!ret.isFinal && proxyExpired < System.currentTimeMillis) throw new InternalProcessingError("Proxy for this job has expired.")
    
    if(ret == ExecutionState.SUBMITTED) {
      val jobShakingInterval = workspace.preferenceAsDurationInMs(GliteEnvironment.JobShakingInterval)

      val probability = {
        if (subState == SubState.RUNNING_SUBMITTED.toString) 
          workspace.preferenceAsDouble(GliteEnvironment.JobShakingProbabilitySubmitted)
        else workspace.preferenceAsDouble(GliteEnvironment.JobShakingProbabilityQueued)
      }
      
      val nbInterval = ((System.currentTimeMillis - lastUpdate.toDouble) / jobShakingInterval)
      if(nbInterval < 1) if(RNG.nextDouble < nbInterval * probability) {
        throw new ShouldBeKilledException("Killed in shaking process")
      } else for(i <- 0 to nbInterval.toInt) if(RNG.nextDouble < probability){
        throw new ShouldBeKilledException("Killed in shaking process")
      }
    }
    ret   
  }
}

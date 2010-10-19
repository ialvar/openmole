/*
 *
 *  Copyright (c) 2007, Cemagref
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation; either version 3 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 *  MA  02110-1301  USA
 */

package org.openmole.plugin.sampling.complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openmole.commons.exception.InternalProcessingError;
import org.openmole.commons.exception.UserBadDataError;
import org.openmole.core.implementation.sampling.Sample;
import org.openmole.core.implementation.sampling.Sampling;
import org.openmole.core.implementation.sampling.Values;
import org.openmole.core.model.job.IContext;
import org.openmole.core.model.sampling.IFactor;
import org.openmole.core.model.sampling.ISample;
import org.openmole.core.model.sampling.IValues;


/**
 *
 * The CompleteSampler enables to generate an exhaustive design of experiment from
 * a set of parameters. It generates all the combinations of the available factors,
 *  which take their values in predefined ranges.
 *
 * @author romain.reuillon@openmole.org
 *
 */
public class CompleteSampling extends Sampling<IFactor<Object, ?>> {

    public CompleteSampling(IFactor<Object, ?>...factors) {
        super(factors);
    }

    @Override
    public ISample build(IContext global, IContext context) throws InternalProcessingError, UserBadDataError {

        List<IValues> listOfListOfValues = new ArrayList<IValues>();

        Iterator[] iterators = new Iterator[getFactors().size()];
        Object[] iteratorsCurrentValue = new Object[getFactors().size()];


        int i = 0;
        for (IFactor<?, ?> factor: getFactors()) {
            factor = getFactors().get(i);
            iterators[i] = factor.getDomain().iterator(global, context);
 
            if(iterators[i].hasNext())
                iteratorsCurrentValue[i] = iterators[i].next();
            else return new Sample(Collections.EMPTY_LIST);

            i++;
        }
        
        boolean end = false;

        // Fetching each list of values
        while (!end) {
            // Append the values to the list
            Values factorValues = new Values();

           i = 0;
            for (IFactor<Object, ?> f : getFactors()) {
                factorValues.setValue(f.getPrototype(), iteratorsCurrentValue[i]);
                i++;
            }

            listOfListOfValues.add(factorValues);

            // preparing the next value
            i = 0;
            for (IFactor<Object, ?> f : getFactors()) {
                if (iterators[i].hasNext()) {
                    iteratorsCurrentValue[i] = iterators[i].next();
                    break;
                } else {
                    if (i == getFactors().size() - 1) {
                        // The last vector has no more values, so the loop is ended
                        end = true;
                        break;
                    }
                    // else, we reset the current vector, and let incrementing the next vector
                    iterators[i] = f.getDomain().iterator(global, context);
                    iteratorsCurrentValue[i] = iterators[i].next();
                }
                i++;
            }
        }
        
        return new Sample(listOfListOfValues);
    }


  
}

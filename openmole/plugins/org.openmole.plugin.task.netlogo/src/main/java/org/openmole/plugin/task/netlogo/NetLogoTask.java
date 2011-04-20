/*
 *  Copyright (C) 2010 reuillon
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.plugin.task.netlogo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.nlogo.api.CompilerException;
import org.nlogo.api.LogoException;
import org.nlogo.headless.HeadlessWorkspace;
import org.openmole.core.implementation.tools.VariableExpansion;
import org.openmole.core.model.data.IPrototype;
import org.openmole.core.model.execution.IProgress;
import org.openmole.core.model.data.IContext;
import org.openmole.misc.exception.InternalProcessingError;
import org.openmole.misc.exception.UserBadDataError;
import org.openmole.misc.workspace.Workspace;
import org.openmole.plugin.task.external.system.ExternalSystemTask;
import scala.Tuple2;
import scala.Tuple3;

/**
 *
 * @author reuillon
 */
public class NetLogoTask extends ExternalSystemTask {

    Iterable<String> launchingCommands;
    List<Tuple2<IPrototype, String>> inputBinding = new LinkedList<Tuple2<IPrototype, String>>();
    List<Tuple3<String, IPrototype, Boolean>> outputBinding = new LinkedList<Tuple3<String, IPrototype, Boolean>>();
    String relativeScriptPath;

    public NetLogoTask(String name,
            File workspace,
            String sriptName,
            Iterable<String> launchingCommands) throws UserBadDataError, InternalProcessingError {
        super(name);
        this.relativeScriptPath = workspace.getName() + "/" + sriptName;
        addResource(workspace);
        this.launchingCommands = launchingCommands;
    }

    public NetLogoTask(String name,
            String workspace,
            String sriptName,
            Iterable<String> launchingCommands) throws UserBadDataError, InternalProcessingError {
        this(name, new File(workspace), sriptName, launchingCommands);
    }

    @Override
    public void process(IContext context, IProgress progress) throws UserBadDataError, InternalProcessingError, InterruptedException {
        try {
            File tmpDir = Workspace.instance().newDir("netLogoTask");
            prepareInputFiles(context, progress, tmpDir);
            File script = new File(tmpDir, relativeScriptPath);
            HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
            try {

                workspace.open(script.getAbsolutePath());

                for (Tuple2<IPrototype, String> inBinding : getInputBinding()) {
                    Object val = context.value(inBinding._1()).get();
                    workspace.command("set " + inBinding._2() + " " + val.toString());
                }

                for (String cmd : launchingCommands) {
                    workspace.command(VariableExpansion.expandData(context, cmd));
                }

                for (Tuple3<String, IPrototype, Boolean> outBinding : getOutputBinding()) {
                    Object outputValue = workspace.report(outBinding._1());
                    if (!outBinding._3()) {
                        context.add(outBinding._2(), outputValue);
                    } else {
                        AbstractCollection netlogoCollection = (AbstractCollection) outputValue;
                        Object array = Array.newInstance(outBinding._2().type().erasure().getComponentType(), netlogoCollection.size());
                        Iterator it = netlogoCollection.iterator();
                        for(int i = 0; i < netlogoCollection.size(); i++) {
                            Array.set(array, i, it.next());
                        }
                        context.add(outBinding._2(), array);
                    }
                }

                fetchOutputFiles(context, progress, tmpDir);
            } catch (CompilerException ex) {
                throw new UserBadDataError(ex);
            } catch (LogoException ex) {
                throw new InternalProcessingError(ex);
            } finally {
                workspace.dispose();
            }
        } catch (IOException e) {
            throw new InternalProcessingError(e);
        }
    }

    public NetLogoTask addNetLogoInput(IPrototype prototype) {
        addInput(prototype);
        return this;
    }

    public NetLogoTask addNetLogoInput(IPrototype prototype, String binding) {
        inputBinding.add(new Tuple2<IPrototype, String>(prototype, binding));
        super.addInput(prototype);
        return this;
    }

    public NetLogoTask addNetLogoOutput(String binding, IPrototype prototype) {
        addNetLogoOutput(binding, prototype, false);
        return this;
    }

    public NetLogoTask addNetLogoOutput(String binding, IPrototype prototype, Boolean toArray) {
        outputBinding.add(new Tuple3<String, IPrototype, Boolean>(binding, prototype, toArray));
        super.addOutput(prototype);
        return this;
    }

    private List<Tuple2<IPrototype, String>> getInputBinding() {
        return inputBinding;
    }

    private List<Tuple3<String, IPrototype, Boolean>> getOutputBinding() {
        return outputBinding;
    }
}

package de.tum.in.i22.pdp.pipcacher;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdpEngine2PipCacher {
    public boolean    eval(IKey key);
    public boolean    eval(IKey key, IEvent event2Simulate);
    public String     getScopeId(IEvent event);
    
    //PIP DIRECT FUNCTION INVOCATION (BYPASS PIPCACHER)
    //Note: use it with care and check for simulation state.
    //using isSimulating()
    public boolean	  isSimulating();
    public Boolean 	  evaluatePredicate(IEvent event, String predicate);
	public Boolean 	  evaluatePredicate(String predicate);
	public Set<IData> getDataInContainer(IContainer container);
	public Set<IContainer> 	getContainerForData(IData data);
	 
}

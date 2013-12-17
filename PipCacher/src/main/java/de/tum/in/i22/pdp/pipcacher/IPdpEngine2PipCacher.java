package de.tum.in.i22.pdp.pipcacher;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPdpEngine2PipCacher {
    public Boolean    eval(IKey key);
    public String     getScopeId();
    
    //PIP DIRECT FUNCTION INVOCATION (BYPASS PIPCACHER)
    public boolean	  isSimulating();
    public Boolean 	  evaluatePredicateCurrentState(String predicate);
	public Set<IData> getDataInContainer(IContainer container);
	public Set<IContainer> 	getContainerForData(IData data);
	
	//Note: use these with care and check for simulation state.
    //      using isSimulating() before executing them
    public Boolean 	  evaluatePredicateSimulatingNextState(IEvent event, String predicate);
    public Boolean 	  evaluatePredicateSimulatingNextState(IEvent event, IKey key);
    public Boolean    eval(IKey key, IEvent event2Simulate);
    
}

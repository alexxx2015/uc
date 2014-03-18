package de.tum.in.i22.pip.core;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPipManager;

public interface IPipCacher2Pip extends IPipManager {

    public IStatus        startSimulation();
    public IStatus        stopSimulation();
    /**
     * If @param event is a desired event, simulates the new state in the PIP, update the cache, and then revert.
     * If @param event is an actual event, does the same, but the PIP remains in the new state.
     * @param event
     * @return
     */
    public ICacheUpdate   refresh(IEvent e);
    public IStatus        addPredicates(Map<String,IKey> predicates);
    public IStatus        revokePredicates(Set<IKey> keys);
    public boolean		  isSimulating();
    
    //PDP FUNCTIONALITIES AVAILABLE FOR DIRECT INVOCATION
    //Use with caution!!!
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate);
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, IKey predicate);
	public Boolean evaluatePredicatCurrentState(String predicate);
	public Set<IContainer> getContainerForData(IData data);
	public Set<IData> getDataInContainer(IContainer container);
	public IStatus notifyActualEvent(IEvent event);
	
    public String getCurrentPipModel();
    
    public void populate(String predicate);
           
}

package de.tum.in.i22.pip.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPipCacher2Pip extends IPipManager {

    public IStatus        startSimulation();
    public IStatus        stopSimulation();
    public ICacheUpdate   refresh(IEvent e);
    public IStatus        addPredicates(Map<String,IKey> predicates);
    public IStatus        revokePredicates(Set<IKey> keys);
    public boolean		  isSimulating();
    
    //PDP FUNCTIONALITIES AVAILABLE FOR DIRECT INVOCATION
    //Use with caution!!!
	public Boolean evaluatePredicate(IEvent event, String predicate);
	public Boolean evaluatePredicate(String predicate);
	public Set<IContainer> getContainerForData(IData data);
	public Set<IData> getDataInContainer(IContainer container);
	public IStatus notifyActualEvent(IEvent event);
           
}

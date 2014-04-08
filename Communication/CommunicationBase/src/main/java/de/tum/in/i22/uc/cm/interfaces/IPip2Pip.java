package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PIP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
public interface IPip2Pip {
    public boolean hasAllData(Set<IData> data);
    public boolean hasAnyData(Set<IData> data);
    public boolean hasAllContainers(Set<IContainer> container);
    public boolean hasAnyContainer(Set<IContainer> container);
    
    /*
     * From PDP & PIP
     */
	public IStatus update(IEvent event);

	/*
	 * From PMP & PIP
	 */
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}

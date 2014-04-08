package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PDP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
public interface IPdp2Pip {
	
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate);
	public boolean evaluatePredicateCurrentState(String predicate);
	public Set<IContainer> getContainersForData(IData data);
	public Set<IData> getDataInContainer(IContainer container);
	IStatus startSimulation();
	IStatus stopSimulation();
	boolean isSimulating();

	/*
     * From PDP & PIP
     */
	public IStatus update(IEvent event);

}

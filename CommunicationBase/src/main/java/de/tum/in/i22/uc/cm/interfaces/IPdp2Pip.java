package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.IConnection;

public interface IPdp2Pip extends IConnection {
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate);
	public Boolean evaluatePredicatCurrentState(String predicate);
	public Set<IContainer> getContainerForData(IData data);
	public Set<IData> getDataInContainer(IContainer container);
	public IStatus notifyActualEvent(IEvent event);
	IStatus startSimulation();
	IStatus stopSimulation();
	boolean isSimulating();
}
package de.tum.in.i22.uc.pip.handlers.pdp;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;

public abstract class Pdp2PipImp extends Connection implements IPdp2Pip {
	public Pdp2PipImp(Connector connector) {
		super(connector);
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event,
			String predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IContainer> getContainerForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus startSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus stopSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimulating() {
		// TODO Auto-generated method stub
		return false;
	}
}

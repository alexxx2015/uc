package de.tum.in.i22.pdp.cm;

import java.util.List;

import de.tum.in.i22.pdp.cm.in.IIncoming;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;

public abstract class CommunicationHandler
		implements IIncoming, ICommunication {
	private InputParser _parser = null;
	private CommunicationManager _commManager = null;
	
	public boolean evaluatePredicate(String par) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public EStatus execute(List<IEvent> events) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<IContainer> getContainerForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}
}

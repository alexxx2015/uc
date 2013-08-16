package de.tum.in.i22.pdp.cm;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface ICommunication {
	public boolean evaluatePredicate(String par);
	public IStatus execute(List<IEvent> events);
	public List<IContainer> getContainerForData(IData data);
	public List<IData> getDataInContainer(IContainer container);
}

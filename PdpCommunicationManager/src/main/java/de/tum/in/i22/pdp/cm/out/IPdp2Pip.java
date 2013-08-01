package de.tum.in.i22.pdp.cm.out;

import java.util.List;

import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;

public interface IPdp2Pip {
	public Boolean evaluatePredicate(IEvent event, String predicate);
	public List<IContainer> getContainerForData(IData data);
	public List<IData> getDataInContainer(IContainer container);
	
	public IResponse notifyActualEvent(IEvent event);
}

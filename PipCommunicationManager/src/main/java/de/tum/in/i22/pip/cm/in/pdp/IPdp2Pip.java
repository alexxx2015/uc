package de.tum.in.i22.pip.cm.in.pdp;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

public interface IPdp2Pip {
	public Boolean evaluatePredicate(IEvent event, String predicate);
	public List<IContainer> getContainerForData(IData data);
	public List<IData> getDataInContainer(IContainer container);
	
	// change IReponse to EStatus
	public IResponse notifyActualEvent(IEvent event);
}

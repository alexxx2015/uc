package de.tum.in.i22.pip.core;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public interface IPdp2Pip {
	public Boolean evaluatePredicate(IEvent event, String predicate);
	public List<IContainer> getContainerForData(IData data);
	public List<IData> getDataInContainer(IContainer container);
	public EStatus notifyActualEvent(IEvent event);
}

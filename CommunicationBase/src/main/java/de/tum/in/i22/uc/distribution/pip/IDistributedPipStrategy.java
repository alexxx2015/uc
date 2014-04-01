package de.tum.in.i22.uc.distribution.pip;

import java.util.Collection;

import de.tum.in.i22.uc.cm.client.Connector;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IDistributedPipStrategy {
	EDistributedPipStrategy getStrategy();

	boolean hasAllData(Connector connector, Collection<IData> data);
	boolean hasAnyData(Connector connector, Collection<IData> data);

	boolean hasAllContainers(Connector connector, Collection<IContainer> containers);
	boolean hasAnyContainer(Connector connector, Collection<IContainer> containers);

	IStatus notifyDataTransfer(Connector connector, IName containerName, Collection<IData> data);
	IStatus notifyActualEvent(Connector connector, IEvent event);
}

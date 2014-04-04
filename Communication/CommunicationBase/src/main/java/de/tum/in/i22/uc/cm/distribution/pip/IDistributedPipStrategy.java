package de.tum.in.i22.uc.cm.distribution.pip;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;

public interface IDistributedPipStrategy {
	EDistributedPipStrategy getStrategy();

	boolean hasAllData(Location location, Set<IData> data);
	boolean hasAnyData(Location location, Set<IData> data);

	boolean hasAllContainers(Location location, Set<IContainer> containers);
	boolean hasAnyContainer(Location location, Set<IContainer> containers);

	IStatus remoteEventUpdate(Location location, IEvent event);

	IStatus remoteDataFlow(Map<Location, Map<IName, Set<IData>>> dataflow);
}

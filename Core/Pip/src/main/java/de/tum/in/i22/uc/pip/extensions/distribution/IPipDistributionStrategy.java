package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;

public interface IPipDistributionStrategy {
	public abstract boolean hasAllData(Location location, Set<IData> data);
	public abstract boolean hasAnyData(Location location, Set<IData> data);

	public abstract boolean hasAllContainers(Location location, Set<IName> containers);
	public abstract boolean hasAnyContainer(Location location, Set<IName> containers);

	public abstract IStatus doRemoteEventUpdate(Location location, IEvent event);

	public abstract IStatus remoteDataFlow(Location srcLocation, Location dstlocation, Map<IName, Set<IData>> dataflow);
}

package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;

public interface IPipDistributionStrategy {
	public boolean hasAllData(Location location, Set<IData> data);
	public boolean hasAnyData(Location location, Set<IData> data);

	public boolean hasAllContainers(Location location, Set<IName> containers);
	public boolean hasAnyContainer(Location location, Set<IName> containers);

	public IStatus doRemoteEventUpdate(Location location, IEvent event);

	public IStatus remoteDataFlow(Location srcLocation, Location dstlocation, Map<IName, Set<IData>> dataflow);

	public Set<Location> whoHasData(Set<IData> data, int recursionDepth);
}

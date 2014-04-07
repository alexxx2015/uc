package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;

public interface IPipDistributionStrategy {
	public abstract boolean hasAllData(Location location, Set<IData> data);
	public abstract boolean hasAnyData(Location location, Set<IData> data);

	public abstract boolean hasAllContainers(Location location, Set<IContainer> containers);
	public abstract boolean hasAnyContainer(Location location, Set<IContainer> containers);

	public abstract IStatus doRemoteEventUpdate(Location location, IEvent event);

	public abstract IStatus doRemoteDataFlow(Location location, Map<IName, Set<IData>> dataflow);
}

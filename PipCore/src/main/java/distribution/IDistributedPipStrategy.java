package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.distribution.ILocation;

public interface IDistributedPipStrategy {
	boolean hasAllData(ILocation location, Collection<IData> data);
	boolean hasAnyData(ILocation location, Collection<IData> data);

	boolean hasAllContainers(ILocation location, Collection<IContainer> containers);
	boolean hasAnyContainer(ILocation location, Collection<IContainer> containers);

	void notifyDataTransfer(ILocation location, IName containerName, Collection<IData> data);
	void notifyConnectionShutdown(ILocation location, IName connectionName);
}

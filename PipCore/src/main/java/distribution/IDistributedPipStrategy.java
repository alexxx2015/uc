package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.Location;

public interface IDistributedPipStrategy {
	boolean hasAllData(Location location, Collection<IData> data);
	boolean hasAnyData(Location location, Collection<IData> data);

	boolean hasAllContainers(Location location, Collection<IContainer> containers);
	boolean hasAnyContainer(Location location, Collection<IContainer> containers);

	void notifyDataTransfer(Location location, IName containerName, Collection<IData> data);
	void notifyConnectionShutdown(Location location, IName connectionName);
}

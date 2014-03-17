package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.Connection;

public interface IDistributedPipStrategy {
	boolean hasAllData(Connection connection, Collection<IData> data);
	boolean hasAnyData(Connection connection, Collection<IData> data);

	boolean hasAllContainers(Connection connection, Collection<IContainer> containers);
	boolean hasAnyContainer(Connection connection, Collection<IContainer> containers);

	void notifyDataTransfer(Connection connection, IName containerName, Collection<IData> data);
	void notifyConnectionShutdown(Connection connection, IName connectionName);
}

package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.AbstractConnection;

public interface IDistributedPipStrategy {
	boolean hasAllData(AbstractConnection connection, Collection<IData> data);
	boolean hasAnyData(AbstractConnection connection, Collection<IData> data);

	boolean hasAllContainers(AbstractConnection connection, Collection<IContainer> containers);
	boolean hasAnyContainer(AbstractConnection connection, Collection<IContainer> containers);

	void notifyDataTransfer(AbstractConnection connection, IName containerName, Collection<IData> data);
	void notifyConnectionShutdown(AbstractConnection connection, IName connectionName);
}

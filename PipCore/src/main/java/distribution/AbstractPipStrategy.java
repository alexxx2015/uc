package distribution;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.distribution.AbstractStrategy;
import de.tum.in.i22.uc.distribution.ConnectionPool;
import de.tum.in.i22.uc.distribution.IConnection;
import de.tum.in.i22.uc.distribution.ILocation;

public class AbstractPipStrategy extends AbstractStrategy implements IDistributedPipStrategy {

	public AbstractPipStrategy(ConnectionPool connectionPool) {
		super(connectionPool);
	}

	@Override
	public boolean hasAllData(ILocation location, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(ILocation location, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(ILocation location,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(ILocation location,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDataTransfer(ILocation location, IName containerName, Collection<IData> data) {
		IConnection connection;
		try {
			connection = obtainConnection(location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}

	@Override
	public void notifyConnectionShutdown(ILocation location,
			IName connectionName) {
		// TODO Auto-generated method stub

	}

}

package distribution;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.distribution.ConnectionPool;
import de.tum.in.i22.uc.distribution.Location;

public class PipPushStrategy extends AbstractPipStrategy {

	@Override
	public boolean hasAllData(Location location, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Location location, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Location location,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Location location,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDataTransfer(Location location, IName containerName, Collection<IData> data) {

	}

	@Override
	public void notifyConnectionShutdown(Location location, IName connectionName) {
		// TODO Auto-generated method stub

	}

}

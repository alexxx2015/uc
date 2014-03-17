package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.Connection;

public class PipPushStrategy extends AbstractPipStrategy {

	@Override
	public boolean hasAllData(Connection connection, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Connection connection, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Connection connection,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Connection connection,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDataTransfer(Connection connection, IName containerName, Collection<IData> data) {

	}

	@Override
	public void notifyConnectionShutdown(Connection connection, IName connectionName) {
		// TODO Auto-generated method stub

	}

}

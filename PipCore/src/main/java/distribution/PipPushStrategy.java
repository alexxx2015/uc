package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.AbstractConnection;

public class PipPushStrategy extends AbstractPipStrategy {

	@Override
	public boolean hasAllData(AbstractConnection connection, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(AbstractConnection connection, Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(AbstractConnection connection,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(AbstractConnection connection,
			Collection<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDataTransfer(AbstractConnection connection, IName containerName, Collection<IData> data) {

	}

	@Override
	public void notifyConnectionShutdown(AbstractConnection connection, IName connectionName) {
		// TODO Auto-generated method stub

	}

}

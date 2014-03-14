package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.distribution.ConnectionPool;
import de.tum.in.i22.uc.distribution.ILocation;

/**
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPip {
	private static final DistributedPip _instance = new DistributedPip();

	private final IDistributedPipStrategy _strategy;

	private DistributedPip() {
		_strategy = new PipPushStrategy(ConnectionPool.getInstance());
	}

	public static DistributedPip getInstance() {
		return _instance;
	}

	public void notifyDataTransfer(ILocation location, IName containerName, Collection<IData> data) {
		_strategy.notifyDataTransfer(location, containerName, data);
	}
}

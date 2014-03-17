package distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.out.Connection;

/**
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipManager {
	private static final DistributedPipManager _instance = new DistributedPipManager();

	private final IDistributedPipStrategy _strategy;

	private DistributedPipManager() {
		_strategy = new PipPushStrategy();
	}

	public static DistributedPipManager getInstance() {
		return _instance;
	}

	public void notifyDataTransfer(Connection connection, IName containerName, Collection<IData> data) {
		_strategy.notifyDataTransfer(connection, containerName, data);
	}
}

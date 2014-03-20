package de.tum.in.i22.pip.core.distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.distribution.pip.IDistributedPipStrategy;

/**
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipManager {
	private static DistributedPipManager _instance;

	private static IDistributedPipStrategy _strategy;

	private DistributedPipManager(EDistributedPipStrategy strategy) {
		_strategy = AbstractPipStrategy.create(strategy);
	}

	public static DistributedPipManager getInstance(EDistributedPipStrategy strategy) {
		if (_instance == null) {
			_instance = new DistributedPipManager(strategy);
		}
		else if (!_strategy.getStrategy().equals(strategy)) {
			throw new RuntimeException("DistributedPipManager was initialized before with strategy: " + _strategy.getStrategy());
		}
		return _instance;
	}


	public static DistributedPipManager getInstance() {
		if (_instance == null) {
			_instance = new DistributedPipManager(EDistributedPipStrategy.DEFAULT_STRATEGY);
		}
		return _instance;
	}


	public IStatus notifyDataTransfer(Connector connector, IName containerName, Collection<IData> data) {
		return _strategy.notifyDataTransfer(connector, containerName, data);
	}

	public IStatus notifyActualEvent(Connector connector, IEvent event) {
		return _strategy.notifyActualEvent(connector, event);
	}
}

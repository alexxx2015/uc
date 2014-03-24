package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.distribution.pip.IDistributedPipStrategy;

/**
 * This class manages the distributed parts of the PIP. To be used as a singleton.
 *
 * The strategy used by this DistributedPipManager instance is determined by
 * {@link Settings#getDistributedPipStrategy()}.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipManager {
	private final static DistributedPipManager _instance = new DistributedPipManager();

	private static IDistributedPipStrategy _strategy;

	private DistributedPipManager() {
		_strategy = DistributedPipStrategy.create(Settings.getInstance().getDistributedPipStrategy());
	}

	public static DistributedPipManager getInstance() {
		return _instance;
	}

	public static EDistributedPipStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	public IStatus notifyDataTransfer(Connector connector, IName containerName, Collection<IData> data) {
		return _strategy.notifyDataTransfer(connector, containerName, data);
	}

	public IStatus notifyActualEvent(Connector connector, IEvent event) {
		return _strategy.notifyActualEvent(connector, event);
	}
}

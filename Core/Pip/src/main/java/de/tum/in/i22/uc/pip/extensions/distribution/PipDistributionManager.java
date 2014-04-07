package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * This class manages the distributed parts of the PIP.
 *
 * The strategy used by this DistributedPipManager instance is determined by
 * {@link Settings#getDistributionStrategy()}.
 *
 * @author Florian Kelbert
 *
 */
public class PipDistributionManager {
	private static PipDistributionStrategy _strategy;

	public PipDistributionManager() {
		_strategy = PipDistributionStrategy.create(Settings.getInstance().getDistributionStrategy());
	}

	public static EDistributionStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	public IStatus remoteDataFlow(Location location, Map<IName,Set<IData>> dataflow) {
		return _strategy.remoteDataFlow(location, dataflow);
	}

	public IStatus update(Location location, IEvent event) {
		return _strategy.remoteEventUpdate(location, event);
	}
}

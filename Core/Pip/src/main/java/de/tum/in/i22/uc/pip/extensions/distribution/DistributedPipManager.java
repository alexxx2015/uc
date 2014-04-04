package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.cm.distribution.pip.IDistributedPipStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * This class manages the distributed parts of the PIP.
 *
 * The strategy used by this DistributedPipManager instance is determined by
 * {@link Settings#getPipDistributionStrategy()}.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipManager {
	private static IDistributedPipStrategy _strategy;

	public DistributedPipManager() {
		_strategy = DistributedPipStrategy.create(Settings.getInstance().getPipDistributionStrategy());
	}

	public static EDistributedPipStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	public IStatus remoteDataFlow(Map<Location,Map<IName,Set<IData>>> dataflow) {
		return _strategy.remoteDataFlow(dataflow);
	}

	public IStatus update(Location location, IEvent event) {
		return _strategy.remoteEventUpdate(location, event);
	}
}

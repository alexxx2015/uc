package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
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
public class PipDistributionManager implements IPipDistributionStrategy {
	private static PipDistributionStrategy _strategy;

	public PipDistributionManager() {
		_strategy = PipDistributionStrategy.create(Settings.getInstance().getDistributionStrategy());
	}

	public static EDistributionStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	@Override
	public IStatus doRemoteDataFlow(Location location, Map<IName,Set<IData>> dataflow) {
		return _strategy.doRemoteDataFlow(location, dataflow);
	}

	@Override
	public IStatus doRemoteEventUpdate(Location location, IEvent event) {
		return _strategy.doRemoteEventUpdate(location, event);
	}

	@Override
	public boolean hasAllData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}
}

package de.tum.in.i22.uc.pmp.extensions.distribution;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributedStrategy;
import de.tum.in.i22.uc.cm.distribution.IDistributedPmpStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * This class manages the distributed parts of the PMP.
 *
 * The strategy used by this DistributedPmpManager instance is determined by
 * {@link Settings#getDistributionStrategy()}.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPmpManager {
	private static IDistributedPmpStrategy _strategy;

	public DistributedPmpManager() {
		_strategy = DistributedPmpStrategy.create(Settings.getInstance().getDistributionStrategy());
	}

	public static EDistributedStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	public IStatus remotePolicyTransfer(Location location, String policy) {
		return _strategy.remotePolicyTransfer(location, policy);
	}
}

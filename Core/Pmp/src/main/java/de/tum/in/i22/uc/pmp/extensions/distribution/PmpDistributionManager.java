package de.tum.in.i22.uc.pmp.extensions.distribution;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
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
public class PmpDistributionManager {
	private static PmpDistributionStrategy _strategy;

	public PmpDistributionManager() {
		_strategy = PmpDistributionStrategy.create(Settings.getInstance().getDistributionStrategy());
	}

	public IStatus remotePolicyTransfer(Location location, Set<String> policies) {
		return _strategy.remotePolicyTransfer(location, policies);
	}
}

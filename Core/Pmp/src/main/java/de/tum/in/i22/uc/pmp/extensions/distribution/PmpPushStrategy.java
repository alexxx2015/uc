package de.tum.in.i22.uc.pmp.extensions.distribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributedStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;

public class PmpPushStrategy extends DistributedPmpStrategy {
	protected static final Logger _logger = LoggerFactory.getLogger(PmpPushStrategy.class);

	public PmpPushStrategy(EDistributedStrategy eStrategy) {
		super(eStrategy);
	}

	@Override
	public IStatus remotePolicyTransfer(Location location, String policy) {
		return null;

	}

}

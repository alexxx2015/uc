package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.datatypes.IStatus;


public interface IDistributedPmpStrategy {
	EDistributedStrategy getStrategy();

	IStatus remotePolicyTransfer(Location location, String policy);
}

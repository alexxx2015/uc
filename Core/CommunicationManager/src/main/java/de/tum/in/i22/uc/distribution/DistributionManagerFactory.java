package de.tum.in.i22.uc.distribution;

import de.tum.in.i22.uc.cm.distribution.IDistributionManager;

public class DistributionManagerFactory {
	public static IDistributionManager createDistributionManager() {
		return new CassandraDistributionManager();
	}
}

package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.distribution.cassandra.CassandraDistributionManager;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDistributionManager;
import de.tum.in.i22.uc.cm.settings.Settings;

public class DistributionManagerFactory {

	public static IDistributionManager createDistributionManager() {
		if (Settings.getInstance().isDistributionEnabled()) {
			return new CassandraDistributionManager();
		}

		return new DummyDistributionManager();
	}
}

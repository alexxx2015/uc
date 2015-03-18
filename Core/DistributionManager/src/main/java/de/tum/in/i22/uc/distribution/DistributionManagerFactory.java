package de.tum.in.i22.uc.distribution;

import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDistributionManager;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.distribution.cassandra.CassandraDistributionManager;

public class DistributionManagerFactory {

	public static IDistributionManager createDistributionManager() {
		if (Settings.getInstance().isDistributionEnabled()) {
			return new CassandraDistributionManager();
		}

		return new DummyDistributionManager();
	}
}

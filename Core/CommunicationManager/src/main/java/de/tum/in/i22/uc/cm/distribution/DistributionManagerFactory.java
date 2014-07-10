package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDistributionManager;
import de.tum.in.i22.uc.cm.settings.Settings;

public class DistributionManagerFactory {

	public static IDistributionManager createDistributionManager() {
		if (Settings.getInstance().getDistributionEnabled()) {
			return new CassandraDistributionManager();
		}

		return new DummyDistributionManager();
	}
}

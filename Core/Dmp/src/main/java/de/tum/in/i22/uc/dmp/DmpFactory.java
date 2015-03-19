package de.tum.in.i22.uc.dmp;

import de.tum.in.i22.uc.cm.processing.DmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyDmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.dmp.cassandra.CassandraDmp;

public class DmpFactory {

	public static DmpProcessor createDmp() {
		if (Settings.getInstance().isDistributionEnabled()) {
			return new CassandraDmp();
		}

		return new DummyDmpProcessor();
	}
}

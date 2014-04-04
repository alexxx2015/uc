package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.cm.distribution.pip.IDistributedPipStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * This class manages the distributed parts of the PIP. To be used as a singleton.
 *
 * The strategy used by this DistributedPipManager instance is determined by
 * {@link Settings#getPipDistributionStrategy()}.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPipManager {
	private static DistributedPipManager _instance;

	private static IDistributedPipStrategy _strategy;

	private DistributedPipManager() {
		_strategy = DistributedPipStrategy.create(Settings.getInstance().getPipDistributionStrategy());
	}

	public static DistributedPipManager getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (DistributedPipManager.class) {
				if (_instance == null) _instance = new DistributedPipManager();
			}
		}
		return _instance;
	}

	public static EDistributedPipStrategy getStrategy() {
		return _strategy.getStrategy();
	}

	public IStatus initialRepresentation(Location location, IName containerName, Set<IData> data) {
		return _strategy.initialRepresentation(location, containerName, data);
	}

	public IStatus update(Location location, IEvent event) {
		return _strategy.update(location, event);
	}
}

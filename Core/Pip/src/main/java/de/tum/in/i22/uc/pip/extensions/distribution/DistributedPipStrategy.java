package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Objects;

import de.tum.in.i22.uc.cm.distribution.AbstractStrategy;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.cm.distribution.pip.IDistributedPipStrategy;

public abstract class DistributedPipStrategy extends AbstractStrategy implements IDistributedPipStrategy {

	private final EDistributedPipStrategy _eStrategy;

	public DistributedPipStrategy(EDistributedPipStrategy eStrategy) {
		_eStrategy = eStrategy;
	}

	@Override
	public final EDistributedPipStrategy getStrategy() {
		return _eStrategy;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof DistributedPipStrategy) {
			return Objects.equals(_eStrategy, ((DistributedPipStrategy) obj)._eStrategy);
		}
		return false;
	}

	static final DistributedPipStrategy create(EDistributedPipStrategy strategy) {
		switch (strategy) {
		case PUSH:
			return new PipPushStrategy(strategy);
		}

		throw new RuntimeException("No such DistributedPipStrategy: " + strategy);
	}
}

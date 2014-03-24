package de.tum.in.i22.uc.pip.core.distribution;

import java.util.Objects;

import de.tum.in.i22.uc.distribution.AbstractStrategy;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.distribution.pip.IDistributedPipStrategy;

public abstract class AbstractDistributedPipStrategy extends AbstractStrategy implements IDistributedPipStrategy {

	private final EDistributedPipStrategy _eStrategy;

	public AbstractDistributedPipStrategy(EDistributedPipStrategy eStrategy) {
		_eStrategy = eStrategy;
	}

	@Override
	public final EDistributedPipStrategy getStrategy() {
		return _eStrategy;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof AbstractDistributedPipStrategy) {
			return Objects.equals(_eStrategy, ((AbstractDistributedPipStrategy) obj)._eStrategy);
		}
		return false;
	}

	static final AbstractDistributedPipStrategy create(EDistributedPipStrategy strategy) {
		switch (strategy) {
		case PUSH:
			return new PipPushStrategy(strategy);
		}

		throw new RuntimeException("No such DistributedPipStrategy: " + strategy);
	}
}

package de.tum.in.i22.uc.cm.distribution;

import java.util.Objects;

public abstract class AbstractStrategy {

	private final EDistributedStrategy _eStrategy;

	public AbstractStrategy(EDistributedStrategy eStrategy) {
		_eStrategy = eStrategy;
	}

	public final EDistributedStrategy getStrategy() {
		return _eStrategy;
	}


	@Override
	public final boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			return Objects.equals(_eStrategy, ((AbstractStrategy) obj)._eStrategy);
		}
		return false;
	}
}

package de.tum.in.i22.uc.cm.distribution;

import java.util.Scanner;

import com.google.common.base.MoreObjects;

public class DistributionGranularity {

	private final EDistributionGranularity _granularity;
	private final int _milliseconds;

	public DistributionGranularity(String s) {
		switch (s.toLowerCase()) {
		case "exact":
			_granularity = EDistributionGranularity.EXACT;
			_milliseconds = -1;
			break;
		case "timestep":
			_granularity = EDistributionGranularity.TIMESTEP;
			_milliseconds = -1;
			break;
		default:
			try {
				Scanner scanner = new Scanner(s);
				_milliseconds = scanner.nextInt();
				scanner.close();
				_granularity = EDistributionGranularity.MILLISECONDS;
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
			break;
		}
	}

	public DistributionGranularity(EDistributionGranularity g) {
		if (g == EDistributionGranularity.MILLISECONDS) {
			throw new IllegalStateException("To set milliseconds, use a different constructor.");
		}

		_granularity = g;
		_milliseconds = -1;
	}

	public DistributionGranularity(EDistributionGranularity g, int ms) {
		if (g != EDistributionGranularity.MILLISECONDS) {
			throw new IllegalStateException("To set not-milliseconds, use a different constructor.");
		}

		_granularity = g;
		_milliseconds = ms;
	}

	public EDistributionGranularity getGranularity() {
		return _granularity;
	}

	public long getMilliseconds() {
		return _milliseconds;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("_granularity", _granularity)
				.add("_milliseconds", _milliseconds)
				.toString();
	}


	public enum EDistributionGranularity {
		EXACT,
		TIMESTEP,
		MILLISECONDS;
	}
}

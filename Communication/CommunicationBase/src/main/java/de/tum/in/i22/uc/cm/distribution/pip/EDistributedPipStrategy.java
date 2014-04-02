package de.tum.in.i22.uc.cm.distribution.pip;


public enum EDistributedPipStrategy {
	PUSH;

	public static final EDistributedPipStrategy DEFAULT_STRATEGY = PUSH;

	private static final String STRATEGY_PUSH = "push";

	public static EDistributedPipStrategy from(String s) {
		switch (s.toLowerCase()) {
		case STRATEGY_PUSH:
			return PUSH;
		}

		throw new RuntimeException("No such distributed pip strategy: " + s);
	}
}

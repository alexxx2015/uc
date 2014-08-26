package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.pdp.core.condition.CircularArray;

class OperatorState {
	boolean value = false;
	boolean immutable = false;

	long counter = 0;
	boolean subEverTrue = false;

	CircularArray<Boolean> circArray = null;

	/**
	 * Indicates whether this operator can be decided locally, or
	 * whether coordination with other PDPs is required.
	 */
	boolean locallyDecidable = true;
}

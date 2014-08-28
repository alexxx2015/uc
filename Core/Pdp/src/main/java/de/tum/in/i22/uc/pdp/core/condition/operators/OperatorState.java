package de.tum.in.i22.uc.pdp.core.condition.operators;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperatorState;

public class OperatorState implements IOperatorState {
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

	private final Operator _operator;

	/**
	 * Creates a new {@link OperatorState}, referring to the
	 * {@link Operator} to which this {@link OperatorState} belongs.
	 *
	 * @param operator the {@link Operator} to which this instance belongs.
	 */
	public OperatorState(Operator operator) {
		_operator = operator;
	}

	@Override
	public boolean value() {
		return value;
	}

	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public long getCounter() {
		return counter;
	}

	@Override
	public boolean isSubEverTrue() {
		return subEverTrue;
	}

	@Override
	public CircularArray<Boolean> getCircArray() {
		return circArray;
	}

	@Override
	public boolean isLocallyDecidable() {
		return locallyDecidable;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("value", value)
				.add("immutable", immutable)
				.add("counter", counter)
				.add("subEverTrue", subEverTrue)
				.add("circArray", circArray)
				.add("locallyDecidable", locallyDecidable)
				.toString();
	}

	@Override
	public IOperator getOperator() {
		return _operator;
	}
}

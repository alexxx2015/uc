package de.tum.in.i22.uc.pdp.core.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.DeepCloneable;

public class State implements DeepCloneable<State> {

	private static final int NO_ENTRIES = StateVariable.values().length;

	private Object[] _values;

	State() {
		_values = new Object[NO_ENTRIES];
	}

	enum StateVariable {
		IMMUTABLE,
		COUNTER,
		VALUE_AT_LAST_TICK,
		ALWAYS_A,
		ALWAYS_A_SINCE_LAST_B,
		SINCE_LAST_TICK,
		CIRC_ARRAY
	}

	<T> void set(StateVariable sv, T value) {
		_values[sv.ordinal()] = value;
	}

	@SuppressWarnings("unchecked")
	<T> T get(StateVariable sv) {
		return (T) _values[sv.ordinal()];
	}

//	@Override
//	protected State clone() {
//		State clone;
//		try {
//			clone = (State) super.clone();
//		} catch (CloneNotSupportedException e) {
//			clone = new State();
//		}
//		clone._values = _values.clone();
//		return clone;
//	}

	@Override
	public State deepClone() {
		State clone;
		try {
			clone = (State) super.clone();
		} catch (CloneNotSupportedException e) {
			clone = new State();
		}

		for (int i = 0; i < _values.length; i++) {
			if (_values[i] instanceof DeepCloneable) {
				clone._values[i] = ((DeepCloneable<?>) _values[i]).deepClone();
			}
			else {
				clone._values[i] = _values[i];
			}
		}

		return clone;
	}
}

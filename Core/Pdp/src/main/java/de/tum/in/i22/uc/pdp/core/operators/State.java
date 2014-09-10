package de.tum.in.i22.uc.pdp.core.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.DeepCloneable;

public class State implements DeepCloneable<State> {

	private static final int NO_ENTRIES = StateVariable.values().length;

	private Object[] _values;

	State() {
		_values = new Object[NO_ENTRIES];
	}

	public enum StateVariable {
		IMMUTABLE,				// usually: type boolean
		VALUE_AT_LAST_TICK,		// usually: type boolean
		ALWAYS_A,				// usually: type boolean
		ALWAYS_A_SINCE_LAST_B,	// usually: type boolean
		SINCE_LAST_TICK,		// usually: type boolean
		COUNTER,				// usually: type long
		CIRC_ARRAY				// usually: type CircularArray
	}

	<T> void set(StateVariable sv, T value) {
		_values[sv.ordinal()] = value;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(StateVariable sv) {
		return (T) _values[sv.ordinal()];
	}

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

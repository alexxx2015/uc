package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.interfaces.DeepCloneable;

final public class State implements DeepCloneable<State> {

	private Object[] _values;

	State() {
		_values = new Object[StateVariable.values().length];
	}

	public enum StateVariable {
		/*
		 * Due to the implementation of deepClone(), it is
		 * wise to enumerate complex types first.
		 */
		CIRC_ARRAY,				// usually: type CircularArray
		IMMUTABLE,				// usually: type boolean
		VALUE_AT_LAST_TICK,		// usually: type boolean
		ALWAYS_A,				// usually: type boolean
		ALWAYS_A_SINCE_LAST_B,	// usually: type boolean
		SINCE_LAST_TICK,		// usually: type boolean
		SINCE_UPDATE,			// usually: type boolean
		RELEVANT,				// usually: type boolean
		COUNTER,				// usually: type long
		COUNT_AT_LAST_TICK		// usually: type long
	}

	final <T extends DeepCloneable<?>> void set(StateVariable sv, T value) {
		_values[sv.ordinal()] = value;
	}

	final void set(StateVariable sv, long value) {
		_values[sv.ordinal()] = value;
	}

	final void set(StateVariable sv, int value) {
		_values[sv.ordinal()] = value;
	}

	final void set(StateVariable sv, boolean value) {
		_values[sv.ordinal()] = value;
	}

	@SuppressWarnings("unchecked")
	final public <T> T get(StateVariable sv) {
		return (T) _values[sv.ordinal()];
	}

	@Override
	final public State deepClone() {
		final State clone = new State();

		List<Thread> startedThreads = new LinkedList<>();

		for (int i = 0; i < _values.length; i++) {
			if (_values[i] instanceof DeepCloneable) {
				/*
				 * Perform more complicated clones in a separate thread.
				 */
				final int j = i;
				Thread t = new Thread() {
					@Override
					public void run() {
						clone._values[j] = ((DeepCloneable<?>) _values[j]).deepClone();
					}
				};
				t.start();
				startedThreads.add(t);
			}
			else {
				clone._values[i] = _values[i];
			}
		}

		// Wait for the started threads to complete
		for (Thread t : startedThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		return clone;
	}

	@Override
	public String toString() {
		return "State: " + Arrays.deepToString(_values);
	}
}

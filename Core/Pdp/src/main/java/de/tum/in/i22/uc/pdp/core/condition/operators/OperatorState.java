//package de.tum.in.i22.uc.pdp.core.condition.operators;
//
//import com.google.common.base.MoreObjects;
//
//import de.tum.in.i22.uc.cm.datatypes.CircularArray;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperatorState;
//
//class OperatorState implements IOperatorState {
//	private boolean value = false;
//	private boolean immutable = false;
//
//	private long counter = 0;
//	private boolean subEverTrue = false;
//
//	private CircularArray<Boolean> circArray = null;
//
//	private final Operator _operator;
//
//	/**
//	 * Creates a new {@link OperatorState}, referring to the
//	 * {@link Operator} to which this {@link OperatorState} belongs.
//	 *
//	 * @param operator the {@link Operator} to which this instance belongs.
//	 */
//	OperatorState(Operator operator) {
//		_operator = operator;
//	}
//
//	boolean value() {
//		return value;
//	}
//
//	boolean isImmutable() {
//		return immutable;
//	}
//
//	long getCounter() {
//		return counter;
//	}
//
//	boolean isSubEverTrue() {
//		return subEverTrue;
//	}
//
//	CircularArray<Boolean> getCircArray() {
//		return circArray;
//	}
//
//	void setValue(boolean value) {
//		this.value = value;
//	}
//
//	void setImmutable(boolean immutable) {
//		this.immutable = immutable;
//	}
//
//	void setCounter(long counter) {
//		this.counter = counter;
//	}
//
//	void decCounter() {
//		this.counter--;
//	}
//
//	void incCounter() {
//		this.counter++;
//	}
//
//	void setSubEverTrue() {
//		this.subEverTrue = true;
//	}
//
//	void setCircArray(CircularArray<Boolean> circArray) {
//		this.circArray = circArray;
//	}
//
//	void newCircArray(long size) {
//		this.circArray = new CircularArray<Boolean>(size);
//	}
//
//	@Override
//	public String toString() {
//		return MoreObjects.toStringHelper(getClass())
//				.add("value", value)
//				.add("immutable", immutable)
//				.add("counter", counter)
//				.add("subEverTrue", subEverTrue)
//				.add("circArray", circArray)
//				.toString();
//	}
//
//	@Override
//	public IOperator getOperator() {
//		return _operator;
//	}
//}

package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface IOperator {

	/**
	 * Returns this {@link IOperator}'s internal identifier as a string.
	 * @return this {@link IOperator}'s internal identifier as a string.
	 */
	public abstract String getFullId();

	/**
	 * Evaluates this operator given the specified event.
	 * If the specified event is null, then this is interpreted
	 * as the end of a timestep and this {@link IOperator} is
	 * evaluated accordingly.
	 *
	 * This method is only to be called on subtypes of this class.
	 * Otherwise, a {@link UnsupportedOperationException} will be thrown.
	 *
	 * @param curEvent
	 * @return
	 */
	public abstract boolean evaluate(IEvent curEvent);

}
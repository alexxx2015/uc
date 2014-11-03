package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface IOperator {

	/**
	 * Returns this {@link IOperator}'s internal identifier as a string.
	 * @return this {@link IOperator}'s internal identifier as a string.
	 */
	public String getFullId();

	/**
	 * Returns the {@link IMechanism} to which this {@link IOperator} belongs.
	 * @return the {@link IMechanism} to which this {@link IOperator} belongs.
	 */
	public IMechanism getMechanism();
}
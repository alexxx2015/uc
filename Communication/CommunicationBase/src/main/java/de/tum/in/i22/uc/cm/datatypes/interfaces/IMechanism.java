package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface IMechanism {

	/**
	 * Returns the name of this {@link IMechanism}.
	 * @return the name of this {@link IMechanism}.
	 */
	public abstract String getName();

	public abstract ICondition getCondition();

	/**
	 * Returns the name of the policy to which this {@link IMechanism} belongs.
	 * @return the name of the policy to which this {@link IMechanism} belongs.
	 */
	public abstract String getPolicyName();

}
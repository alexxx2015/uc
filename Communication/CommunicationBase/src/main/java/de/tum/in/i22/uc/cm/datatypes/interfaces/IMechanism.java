package de.tum.in.i22.uc.cm.datatypes.interfaces;


public interface IMechanism {

	/**
	 * Returns the name of this {@link Mechanism}.
	 * @return the name of this {@link Mechanism}.
	 */
	public abstract String getName();

	public abstract ICondition getCondition();

	/**
	 * Returns the name of the policy to which this {@link Mechanism} belongs.
	 * @return the name of the policy to which this {@link Mechanism} belongs.
	 */
	public abstract String getPolicyName();

}
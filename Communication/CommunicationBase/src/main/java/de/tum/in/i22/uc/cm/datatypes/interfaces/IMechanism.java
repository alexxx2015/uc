package de.tum.in.i22.uc.cm.datatypes.interfaces;

public interface IMechanism {

	/**
	 * Returns the name of this {@link IMechanism}.
	 * @return the name of this {@link IMechanism}.
	 */
	public String getName();

	public ICondition getCondition();

	/**
	 * Returns the name of the policy to which this {@link IMechanism} belongs.
	 * @return the name of the policy to which this {@link IMechanism} belongs.
	 */
	public String getPolicyName();

	public long getLastUpdate();

	public long getTimestepSize();

}
package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;

public interface IPdp2Dmp {

	public void notify(IOperator operator, boolean endOfTimestep);


	void setFirstTick(String policyName, String mechanismName, long firstTick);
	
	/**
	 * Returns the point in time at which the specified Mechanism was first tick()ed.
	 * If the Mechanism never tick() before, {@link Long#MIN_VALUE} is returned.
	 *
	 * @param policyName the name of the policy to which the mechanism belongs.
	 * @param mechanismName the mechanism we are asking for.
	 * @return
	 */
	long getFirstTick(String policyName, String mechanismName);

	boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep);

	int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep);

	int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep);
}

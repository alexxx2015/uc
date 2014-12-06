package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public interface IDistributionManager {

	/**
	 * Invoked whenever a remote data transfer happens.
	 * @param dataflow
	 */
	void dataTransfer(RemoteDataFlowInfo dataflow);

	void init(PdpProcessor _pdp, PipProcessor _pip, PmpProcessor _pmp);

	/**
	 * Method to be invoked whenever a new policy name ought to be managed.
	 *
	 * @param policy the policy to be managed.
	 */
	void register(XmlPolicy policy);
	
	void register(XmlPolicy policy, String from);

	public void notify(IOperator operator, boolean endOfTimestep);

//	boolean wasNotifiedSince(AtomicOperator operator, long since);
//
//	boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to);
//
//	public int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to);

	void deregister(String policyName, IPLocation location);

	IPLocation getResponsibleLocation(String ip);

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

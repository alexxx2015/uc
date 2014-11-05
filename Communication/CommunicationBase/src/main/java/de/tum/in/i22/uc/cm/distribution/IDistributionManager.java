package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
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
	public void registerPolicy(XmlPolicy policy);

	void update(IResponse res);

	boolean wasTrueSince(AtomicOperator operator, long since);

	boolean wasTrueInBetween(AtomicOperator operator, long from, long to);

	public long howOftenTrueInBetween(AtomicOperator operator, long from, long to);

	void unregisterPolicy(String policyName, IPLocation location);

	IPLocation getResponsibleLocation(String ip);
}

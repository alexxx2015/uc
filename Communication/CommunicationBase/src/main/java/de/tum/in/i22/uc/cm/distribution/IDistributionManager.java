package de.tum.in.i22.uc.cm.distribution;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
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
	 * Method to be invoked whenever a new policy ought to be managed.
	 *
	 * @param xmlPolicy
	 */
	void newPolicy(XmlPolicy xmlPolicy);
}

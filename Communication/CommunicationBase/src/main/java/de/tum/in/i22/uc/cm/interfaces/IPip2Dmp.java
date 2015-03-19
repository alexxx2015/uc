package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;

public interface IPip2Dmp {
	/**
	 * Invoked whenever a remote data transfer happens.
	 * @param dataflow
	 */
	void doDataTransfer(RemoteDataFlowInfo dataflow);
	
	IPLocation getResponsibleLocation(String ip);
}

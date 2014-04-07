package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;



/**
 * Interface defining methods to be invoked on a PMP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pmp {
	/*
	 * From _local_ PIP
	 */
	IStatus informRemoteDataFlow(Map<Location, Map<IName, Set<IData>>> dataflow);

	/*
	 * From PMP
	 */
	IStatus remotePolicyTransfer(Set<String> policies);
}

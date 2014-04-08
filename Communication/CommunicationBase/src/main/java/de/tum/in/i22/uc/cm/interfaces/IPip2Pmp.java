package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;


/**
 * Interface defining methods a PIP can invoke on a PMP.
 * @author Kelbert & Lovat
 *
 */
public interface IPip2Pmp {
	/**
	 * Called from a PIP to tell the PMP that some remote data flow happened.
	 * The PMP is then in charge of transferring the corresponding policies.
	 *
	 * @param location the location to which the data was transferred.
	 * @param dataflow the data that was transferred
	 * @return
	 */
	IStatus informRemoteDataFlow(Location location, Set<IData> dataflow);
}

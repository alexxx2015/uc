package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;


/**
 * Interface defining methods a PIP can invoke on a PMP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPip2Pmp")
public interface IPip2Pmp {
	/**
	 * Called from a PIP to tell the PMP that some remote data flow happened.
	 * The PMP is then in charge of transferring the corresponding policies.
	 *
	 * @param location the location to which the data was transferred.
	 * @param dataflow the data that was transferred
	 * @return
	 */
	@AThriftMethod(signature="Types.TStatus informRemoteDataFlow(1: string address, 2: Types.int port, 3: set<Types.TData> data)")
	public IStatus informRemoteDataFlow(Location location, Set<IData> dataflow);
}

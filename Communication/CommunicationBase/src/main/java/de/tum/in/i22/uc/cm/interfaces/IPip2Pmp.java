//package de.tum.in.i22.uc.cm.interfaces;
//
//import java.util.Set;
//
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
//import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
//import de.tum.in.i22.uc.cm.distribution.Location;
//import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
//import de.tum.in.i22.uc.thrift.generator.AThriftService;
//
//
///**
// * Interface defining methods a PIP can invoke on a PMP.
// * @author Kelbert & Lovat
// *
// */
//@AThriftService(name="TPip2Pmp")
//public interface IPip2Pmp {
//
//	/**
//	 * Called from a PIP to tell the PMP that some remote data flow happened.
//	 * The PMP is then in charge of transferring the corresponding policies.
//	 *
//	 * @param location the location to which the data was transferred.
//	 * @param dataflow the data that was transferred
//	 * @return
//	 */
//	@AThriftMethod(signature="Types.TStatus informRemoteDataFlow(1: string srcAddress, 2: Types.int srcPort, 3: string dstAddress, 4: Types.int dstPort, 5: set<Types.TData> data)")
//	public IStatus informRemoteDataFlow(Location srcLocation, Location dstLocation, Set<IData> dataflow);
//}

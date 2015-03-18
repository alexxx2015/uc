package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods between remote DistributionManagers.
 * @author Kelbert
 *
 */
@AThriftService(name="TDistr2Distr")
public interface IDistr2Distr {
	
	@AThriftMethod(signature="Types.TStatus remoteTransfer(1: set<Types.TXmlPolicy> policies, 2: string fromHost, 3: Types.TName containerName, 4: set<Types.TData> data)")
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost, IName containerName, Set<IData> data);
}

package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PMP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Pip")
public interface IPmp2Pip {
	@AThriftMethod(signature="Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)")
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}

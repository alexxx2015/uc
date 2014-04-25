package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PEP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPep2Pdp")
public interface IPep2Pdp {
	@AThriftMethod(signature="oneway void notifyEventAsync(1: Types.TEvent pepEvent)")
	public void notifyEventAsync(IEvent pepEvent);

	@AThriftMethod(signature="Types.TResponse notifyEventSync(1: Types.TEvent pepEvent)")
	public IResponse notifyEventSync(IEvent pepEvent);
}

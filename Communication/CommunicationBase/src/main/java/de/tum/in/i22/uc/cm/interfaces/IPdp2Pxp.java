package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PDP can invoke on a PXP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPdp2Pxp")
public interface IPdp2Pxp {
	@AThriftMethod(signature="Types.TStatus executeSync(1: list<Types.TEvent> eventList)")
	public IStatus executeSync(List<IEvent> eventList);

	@AThriftMethod(signature="oneway void executeAsync(1: list<Types.TEvent> eventList)")
	public void executeAsync(List<IEvent> eventList);
}

package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PIP can invoke on a Java PIP.
 * @author Lovat
 *
 */
@AThriftService(name="TPip2JPip")
public interface IPip2JPip {
	@AThriftMethod(signature="oneway void notifyAsync(1: Types.TEvent updateEvent)")
	public void notifyAsync(IEvent updateEvent);

}

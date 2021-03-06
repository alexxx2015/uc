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
	
	
	/**
	 * UC4WIN interface.
	 * Notice that while the thrift interface has also the senderId parameter, the Java method does not.
	 * On the implementation side, the senderID is embodied as another parameter in the event.
	 */
	
	@AThriftMethod(signature = "oneway void processEventAsync(1: Types.TobiasEvent e, 2: string senderID)")
	public void processEventAsync(IEvent pepEvent);

	/**
	 * UC4WIN interface.
	 * Notice that while the thrift interface has also the senderId parameter, the Java method does not.
	 * On the implementation side, the senderID is embodied as another parameter in the event.
	 */
	@AThriftMethod(signature = "Types.TobiasResponse processEventSync(1: Types.TobiasEvent e, 2: string senderID)")
	public IResponse processEventSync(IEvent pepEvent);
		
}

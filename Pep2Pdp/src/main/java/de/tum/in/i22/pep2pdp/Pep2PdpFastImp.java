package de.tum.in.i22.pep2pdp;

import java.io.IOException;
import java.io.OutputStream;

import de.tum.in.i22.pdp.cm.out.FastConnector;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;

public class Pep2PdpFastImp extends FastConnector implements IPep2PdpFast {

 	public Pep2PdpFastImp(String address, int port) {
 		super(address, port);
	}
 	
 	@Override
	public IResponse notifyEvent(IEvent event) {
 		_logger.debug("notifyEvent entered");
 		
		_logger.trace("Create Google Protocol Buffer event instance.");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		try {
			OutputStream out = getOutputStream();
			// write one dummy byte, this byte can be later used to encode the operation
			// currently we have only one operation
			out.write(0);
			gpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("Event written to OutputStream.");
			
			_logger.trace("Wait for GpResponse");
			GpResponse gpResponse = GpResponse.parseDelimitedFrom(getInputStream());
			return new ResponseBasic(gpResponse);
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
			return null;
		}
	}
}

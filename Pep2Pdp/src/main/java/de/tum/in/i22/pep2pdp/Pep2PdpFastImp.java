package de.tum.in.i22.pep2pdp;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.out.FastConnector;

public class Pep2PdpFastImp extends FastConnector implements IPep2PdpFast {

	
	private static final Logger _logger = Logger
			.getLogger(Pep2PdpFastImp.class);
	
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

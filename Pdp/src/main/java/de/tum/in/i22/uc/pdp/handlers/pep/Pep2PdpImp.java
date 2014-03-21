package de.tum.in.i22.uc.pdp.handlers.pep;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;

public class Pep2PdpImp extends Connection implements IPep2Pdp {
	public Pep2PdpImp(Connector connector) {
		super(connector);
	}

	@Override
	public Object notifyEvent(IEvent event) {
// 		_logger.debug("notifyEvent method entered");
//
//		_logger.trace("Create Google Protocol Buffer event instance");
//		GpEvent gpEvent = EventBasic.createGpbEvent(event);
//		try {
//			OutputStream out = getOutputStream();
//			out.write(EPep2PdpMethod.NOTIFY_EVENT.getValue());
//
//			gpEvent.writeDelimitedTo(out);
//			out.flush();
//			_logger.trace("Event written to OutputStream");
//
//			_logger.trace("Wait for GpResponse");
//			GpResponse gpResponse = GpResponse.parseDelimitedFrom(getInputStream());
//
//			return new ResponseBasic(gpResponse);
//		} catch (IOException ex) {
//			_logger.error("Failed to notify event.", ex);
//			//TODO better throw custom unchecked exception than return null
			return null;
//	}
	}
}

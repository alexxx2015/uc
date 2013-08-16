package de.tum.in.i22.uc.cm.in;

import java.util.Map;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.StatusBasic;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;

/**
 * Google Protocol Buffer Message Factory
 * @author Stoimenov
 *
 */
public class MessageFactory implements IMessageFactory {
	
	public static IMessageFactory getInstance() {
		return new MessageFactory();
	}

	@Override
	public IEvent createEvent(String name, Map<String, String> map) {		
		IEvent e =  new EventBasic(name, map);
		return e;
	}
	
	@Override
	public IEvent createEvent(GpEvent gpEvent) {
		EventBasic e = new EventBasic(gpEvent);
		return e;
	}
	
	@Override
	public IEvent createEvent(GpEvent gpEvent, long timestamp) {
		EventBasic e = new EventBasic(gpEvent);
		e.setTimestamp(timestamp);
		return e;
	}
	
	@Override
	public IResponse createError1Response() {
		IStatus status = new StatusBasic(EStatus.ERROR1, null);
		IResponse response = new ResponseBasic(status, null, null);
		return response;
	}
}

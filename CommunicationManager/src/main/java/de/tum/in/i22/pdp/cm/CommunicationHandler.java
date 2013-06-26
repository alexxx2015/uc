package de.tum.in.i22.pdp.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.pdp.cm.in.IIncoming;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

/**
 * This is just a stub for now
 * @author Stoimenov
 *
 */
public class CommunicationHandler
		implements IIncoming {
	
	private static CommunicationHandler _instance;
	
	private static IMessageFactory _factory = MessageFactory.getInstance();
	
	public static CommunicationHandler getInstance() {
		if (_instance == null) {
			_instance = new CommunicationHandler();
		}
		return _instance;
	}
	
	private CommunicationHandler() {
		
	}

	@Override
	public EStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return EStatus.OKAY;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EStatus revokeMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		EStatus status = EStatus.ALLOW;
		
		List<IEvent> executeActions = new ArrayList<IEvent>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("key1", "val1");
		map1.put("key2", "val2");
		IEvent action1 = _factory.createEvent("event1", map1);
		IEvent action2 = _factory.createEvent("event2", map1);
		executeActions.add(action1);
		executeActions.add(action2);
		
		IEvent modifiedEvent = _factory.createEvent("eventModified", map1);
		
		ResponseBasic response = new ResponseBasic(status, executeActions, modifiedEvent);
		return response;
	}
}

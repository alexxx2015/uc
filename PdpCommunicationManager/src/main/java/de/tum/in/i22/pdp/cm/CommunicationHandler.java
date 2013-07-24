package de.tum.in.i22.pdp.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.pdp.cm.in.IIncoming;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ConditionBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.datatypes.basic.SimplifiedTemporalLogicBasic;
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
		// TODO implement
		return EStatus.OKAY;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		MechanismBasic m = new MechanismBasic();
		
		
		//* set condition
		ConditionBasic condition = new ConditionBasic();
		//** set condition condition
		OslFormulaBasic formula = new OslFormulaBasic("Formula xxxx");
		condition.setCondition(formula);
		//** set condition conditionSimp
		SimplifiedTemporalLogicBasic conditionSimp = new SimplifiedTemporalLogicBasic();
		conditionSimp.setFormula(new OslFormulaBasic("Formula yyyy"));
		
		//*** set condition conditionSimp dataEventMap
		Map<IData, IEvent> map1 = new HashMap<IData, IEvent>();
		map1.put(new DataBasic("id1"), new EventBasic("event1", null));
		DataEventMapBasic dataEventMap = new DataEventMapBasic(map1);
		conditionSimp.setDataEventMap(dataEventMap);
		condition.setConditionSimp(conditionSimp);
		m.setCondition(condition);
		return m;
	}

	@Override
	public EStatus revokeMechanism(String par) {
		//TODO implement
		return EStatus.OKAY;
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		//TODO implements
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

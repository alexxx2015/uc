package de.tum.in.i22.pdp.datatypes.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.ICondition;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IHistory;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;

public class MechanismBasic implements IMechanism {
	private static Logger _logger = Logger.getLogger(MechanismBasic.class);
	private ICondition _condition;
	private String _mechanismName;
	private IResponse _response;
	private IHistory _state;
	private IEvent _triggerEvent;
	
	public MechanismBasic(GpMechanism gpM) {
		if (gpM == null)
			return;
		
		_mechanismName = gpM.getMechanismName();
		_condition = new ConditionBasic(gpM.getCondition());
		_response = new ResponseBasic(gpM.getResponse());		
		_state = new HistoryBasic(gpM.getState());
		_triggerEvent = new EventBasic(gpM.getTriggerEvent());
	}

	@Override
	public String getMechanismName() {
		return _mechanismName;
	}

	@Override
	public ICondition getCondition() {
		return _condition;
	}

	@Override
	public IResponse getResponse() {
		return _response;
	}

	@Override
	public IHistory getState() {
		return _state;
	}

	@Override
	public IEvent getTriggerEvent() {
		return _triggerEvent;
	}
	
	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer object corresponding to IMechanism
	 */
	public static GpMechanism createGpbMechanism(IMechanism m) {
		_logger.trace("Build GpMechanism");
		GpMechanism.Builder gp = GpMechanism.newBuilder();
		
		_logger.trace("Build condition");
		gp.setCondition(
				ConditionBasic.createGpbCondition(
						m.getCondition()));
		
		gp.setMechanismName(m.getMechanismName());
		_logger.trace("Build response");
		gp.setResponse(
				ResponseBasic.createGpbResponse(
						m.getResponse()));
		
		_logger.trace("Build state");
		gp.setState(
				HistoryBasic.createGpbHistory(
						m.getState()));
		
		_logger.trace("Build trigger event");
		gp.setTriggerEvent(
				EventBasic.createGpbEvent(
						m.getTriggerEvent()));
		
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			MechanismBasic o = (MechanismBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_condition, o.getCondition())
				&& CompareUtil.areObjectsEqual(_mechanismName, o.getMechanismName())
				&& CompareUtil.areObjectsEqual(_response, o.getResponse())
				&& CompareUtil.areObjectsEqual(_state, o.getState())
				&& CompareUtil.areObjectsEqual(_triggerEvent, o.getTriggerEvent());
		}
		return isEqual;
	}
	
}

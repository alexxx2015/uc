package de.tum.in.i22.pdp.datatypes.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.ICondition;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IHistory;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpCondition;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpHistory;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;

public class MechanismBasic implements IMechanism {
	private static Logger _logger = Logger.getLogger(MechanismBasic.class);
	private ICondition _condition;
	private String _mechanismName;
	private IResponse _response;
	private IHistory _state;
	private IEvent _triggerEvent;
	private String _xml;
	
	public MechanismBasic() {
	}
	
	public MechanismBasic(String xml) {
		_xml = xml;
		//TODO implement: create mechanism from xml
	}
	
	@Override
	public String toXML() {
		return _xml;
	}
	
	public MechanismBasic(GpMechanism gpM) {
		if (gpM == null)
			return;
		
		if (gpM.hasMechanismName())
			_mechanismName = gpM.getMechanismName();
		if (gpM.hasCondition())
			_condition = new ConditionBasic(gpM.getCondition());
		if (gpM.hasResponse())
			_response = new ResponseBasic(gpM.getResponse());
		if (gpM.hasState())
			_state = new HistoryBasic(gpM.getState());
		if (gpM.hasTriggerEvent())
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

	public void setCondition(ICondition condition) {
		_condition = condition;
	}

	public void setMechanismName(String mechanismName) {
		_mechanismName = mechanismName;
	}

	public void setResponse(IResponse response) {
		_response = response;
	}

	public void setState(IHistory state) {
		_state = state;
	}

	public void setTriggerEvent(IEvent triggerEvent) {
		_triggerEvent = triggerEvent;
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
		
		GpCondition gpCondition = ConditionBasic.createGpbCondition(
				m.getCondition());
		if (gpCondition != null)
			gp.setCondition(gpCondition);
		
		String mechanismName = m.getMechanismName();
		if (mechanismName != null)
			gp.setMechanismName(mechanismName);
		
		_logger.trace("Build response");
		GpResponse gpResponse = ResponseBasic.createGpbResponse(
				m.getResponse());
		if (gpResponse != null)
			gp.setResponse(gpResponse);
		
		_logger.trace("Build state");
		GpHistory gpHistory = HistoryBasic.createGpbHistory(
				m.getState());
		if (gpHistory != null)
			gp.setState(gpHistory);
		
		_logger.trace("Build trigger event");
		GpEvent gpEvent = EventBasic.createGpbEvent(
				m.getTriggerEvent());
		if (gpEvent != null)
			gp.setTriggerEvent(gpEvent);
		
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

	@Override
	public String toString() {
		return "MechanismBasic [_condition=" + _condition + ", _mechanismName="
				+ _mechanismName + ", _response=" + _response + ", _state="
				+ _state + ", _triggerEvent=" + _triggerEvent + "]";
	}
	
	
	
}

package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.ICondition;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IHistory;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCondition;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpHistory;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;

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
		if (obj instanceof MechanismBasic) {
			MechanismBasic o = (MechanismBasic)obj;
			isEqual = Objects.equals(_condition, o._condition)
				&& Objects.equals(_mechanismName, o._mechanismName)
				&& Objects.equals(_response, o._response)
				&& Objects.equals(_state, o._state)
				&& Objects.equals(_triggerEvent, o._triggerEvent);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_condition, _mechanismName, _response, _state, _triggerEvent);
	}

	@Override
	public String toString() {
		return "MechanismBasic [_condition=" + _condition + ", _mechanismName="
				+ _mechanismName + ", _response=" + _response + ", _state="
				+ _state + ", _triggerEvent=" + _triggerEvent + "]";
	}



}

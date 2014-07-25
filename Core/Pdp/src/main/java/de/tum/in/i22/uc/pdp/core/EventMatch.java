package de.tum.in.i22.uc.pdp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.EventMatchingOperatorType;
import de.tum.in.i22.uc.pdp.xsd.ParamMatchType;

public class EventMatch extends EventMatchingOperatorType {
	private static Logger _logger = LoggerFactory.getLogger(EventMatch.class);

	public EventMatch() {
	}

	public EventMatch(EventMatchingOperatorType op, Mechanism curMechanism) {
		_logger.debug("Preparing eventMatch from EventMatchingOperatorType");
		_pdp = curMechanism.getPolicyDecisionPoint();
		this.setAction(op.getAction());
		this.setTryEvent(op.isTryEvent());
		for (ParamMatchType paramMatch : op.getParams()) {
			// if paramMatch.getType()=
			// initialization
			this.getParams().add(paramMatch);
		}
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
	}

	public boolean eventMatches(IEvent curEvent) {
		if (curEvent == null)
			return false;
		_logger.info("Matching [{}] against [{}]", this, curEvent);
		if (this.isTryEvent() != curEvent.isActual()) {
			if (this.getAction().equals(curEvent.getName())
					|| this.getAction().equals(Settings.getInstance().getStarEvent())) {
				if (this.getParams().size() == 0)
					return true;
				boolean ret = false;
				for (ParamMatchType p : this.getParams()) {
					ParamMatch curParamMatch = ParamMatch.createFrom(p, _pdp);
					_logger.debug("Matching param [{}]", p);
					ret = curParamMatch.paramMatches(p.getName(), curEvent.getParameterValue(p.getName()));
					if (!ret)
						break;
				}
				return ret;
			}
		}
		_logger.info("Event does NOT match.");
		return false;
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_logger.error("Operator evaluation was triggered for EventMatch instead of EventMatchOperator?!");
		return false;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("action", action)
				.add("isTry", isTryEvent())
				.add("params", getParams())
				.toString();
	}

}

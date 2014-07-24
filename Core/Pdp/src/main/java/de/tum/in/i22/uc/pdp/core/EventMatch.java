package de.tum.in.i22.uc.pdp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.shared.Event;
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

	public boolean eventMatches(Event curEvent) {
		if (curEvent == null)
			return false;
		_logger.info("Matching      [{}]", this);
		_logger.info("against event [{}]", curEvent);
		if (this.isTryEvent() != curEvent.isActual()) {
			if (this.getAction().equals(curEvent.getName())
					|| this.getAction().equals(Settings.getInstance().getStarEvent())) {
				if (this.getParams().size() == 0)
					return true;
				boolean ret = false;
				for (ParamMatchType p : this.getParams()) {
					ParamMatch curParamMatch = (ParamMatch) p;
					_logger.debug("Matching param [{}]", p);
					_logger.debug("setting pdp for current parameter");
					curParamMatch.setPdp(_pdp);
					ret = curParamMatch.paramMatches(curEvent.getParameter(p.getName()));
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
	public boolean evaluate(Event curEvent) {
		_logger.error("Operator evaluation was triggered for EventMatch instead of EventMatchOperator?!");
		return false;
	}

	@Override
	public String toString() {
		String str = "eventMatch action='" + this.getAction() + "' isTry='" + this.isTryEvent() + "': [";
		for (ParamMatchType p : this.getParams()) {
			ParamMatch p2 = (ParamMatch) p;
			str += p2.toString() + ", ";
		}
		str += "]";
		return str;
	}

}

package de.tum.in.i22.uc.pdp.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.xsd.EventMatchingOperatorType;
import de.tum.in.i22.uc.pdp.xsd.ParamMatchType;

public class EventMatch extends EventMatchingOperatorType {
	private static Logger log = LoggerFactory.getLogger(EventMatch.class);

	public EventMatch() {
	}

	public EventMatch(EventMatchingOperatorType op, Mechanism curMechanism) {
		log.debug("Preparing eventMatch from EventMatchingOperatorType");
		this.setAction(op.getAction());
		this.setTryEvent(op.isTryEvent());
		for (ParamMatchType paramMatch : op.getParams()) {
			this.getParams().add(paramMatch);
		}
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
	}

	public boolean eventMatches(Event curEvent) {
		if (curEvent == null)
			return false;
		log.info("Matching      [{}]", this);
		log.info("against event [{}]", curEvent);
		if (this.isTryEvent() == curEvent.isTryEvent()) {
			if (this.getAction().equals(curEvent.getEventAction())) {
				if (this.getParams().size() == 0)
					return true;
				boolean ret = false;
				for (ParamMatchType p : this.getParams()) {
					ParamMatch curParamMatch = (ParamMatch) p;
					log.debug("Matching param [{}]", p);
					ret = curParamMatch.paramMatches(curEvent
							.getParameterForName(p.getName()));
					if (!ret)
						break;
				}
				return ret;
			}
		}
		log.info("Event does NOT match.");
		return false;
	}

	@Override
	public boolean evaluate(Event curEvent) {
		log.error("Operator evaluation was triggered for EventMatch instead of EventMatchOperator?!");
		return false;
	}

	@Override
	public String toString() {
		String str = "eventMatch action='" + this.getAction() + "' isTry='"	+ this.isTryEvent() + "': [";
		for (ParamMatchType p : this.getParams()) {
			ParamMatch p2 = (ParamMatch) p;
			str += p2.toString() + ", ";
		}
		str += "]";
		return str;
	}

}

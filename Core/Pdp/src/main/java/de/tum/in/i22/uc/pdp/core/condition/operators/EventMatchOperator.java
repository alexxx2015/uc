package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.ActionDescriptionStore;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public class EventMatchOperator extends EventMatch {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	public EventMatchOperator() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		ActionDescriptionStore ads = _pdp.getActionDescriptionStore();
		ads.addEventMatch(this);
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		boolean ret = false;
		if (curEvent != null) {
			if (eventMatches(curEvent))
				_state.value = true;
			ret = _state.value;
		} else { // reset at end of timestep (i.e. curEvent==null)
			ret = _state.value;
			_state.value = false;
		}
		_logger.debug("eval EVENTMATCH [{}]", ret);
		return ret;
	}
}

package de.tum.in.i22.uc.pdp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.operators.Operator;
import de.tum.in.i22.uc.pdp.xsd.EventMatchingOperatorType;
import de.tum.in.i22.uc.pdp.xsd.ParamMatchType;

public class EventMatch extends EventMatchingOperatorType {
	private static Logger _logger = LoggerFactory.getLogger(EventMatch.class);

	public EventMatch() {
	}

	public static EventMatch convertFrom(EventMatchingOperatorType e, PolicyDecisionPoint pdp) {
		if (e instanceof EventMatch) {
			EventMatch newe = (EventMatch) e;
			newe._pdp = pdp;
			return newe;
		}

		throw new IllegalArgumentException(e + " is not of type " + EventMatch.class);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	public boolean matches(IEvent ev) {
		if (ev == null) {
			return false;
		}

		_logger.info("Matching [{}] against [{}]", this, ev);

		/*
		 *  Be aware: tryEvent vs. isActual must be unequal (!=).
		 */
		if (tryEvent != ev.isActual()) {
			if (action.equals(ev.getName()) || action.equals(Settings.getInstance().getStarEvent())) {
				if (params == null || params.size() == 0) {
					_logger.info("Event DOES match.");
					return true;
				}

				/*
				 * Compare each parameter
				 */
				for (ParamMatchType p : params) {
					_logger.debug("Matching param [{}]", p);
					if (!ParamMatch.convertFrom(p, _pdp).matches(p.getName(), ev.getParameterValue(p.getName()))) {
						_logger.info("Event does NOT match.");
						return false;
					}
				}
				_logger.info("Event DOES match.");
				return true;
			}
		}

		_logger.info("Event does NOT match.");
		return false;
	}


	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("action", action)
				.add("isActual", !isTryEvent())
				.add("params", getParams())
				.toString();
	}

}

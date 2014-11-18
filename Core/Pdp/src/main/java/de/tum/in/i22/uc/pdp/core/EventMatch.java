package de.tum.in.i22.uc.pdp.core;

import java.util.Iterator;

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

		boolean result = true;

		/*
		 *  Be aware: tryEvent vs. isActual must be unequal (!=).
		 */
		if (tryEvent == ev.isActual()
					|| (!action.equals(ev.getName()) && !action.equals(Settings.getInstance().getStarEvent()))) {
			result = false;
		}
		else if (params != null) {
			Iterator<ParamMatchType> it = params.iterator();
			while (result && it.hasNext()) {
				ParamMatch p = ParamMatch.convertFrom(it.next(), _pdp);
				_logger.debug("Matching param [{}]", p);

				if (!p.matches(p.getName(), ev.getParameterValue(p.getName()))) {
					result = false;
				}
			}
		}

		_logger.info("Event {} match.", result ? "DOES" : "does NOT");
		return result;
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

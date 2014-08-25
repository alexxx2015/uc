package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.NotType;

public class OSLNot extends NotType {
	private static Logger log = LoggerFactory.getLogger(OSLNot.class);

	public OSLNot() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		/*
		 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
		 * At this place, we check whether the operand of not(.) is a Literal. If this is not
		 * the case, an IllegalArgumentException is thrown.
		 */
		if (Settings.getInstance().getDistributionEnabled() && !(operators instanceof LiteralOperator)) {
			throw new IllegalArgumentException(
					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
							+ this.getClass() + " was not of type " + LiteralOperator.class + ").");
		}

		((Operator) operators).init(mech);
	}

	@Override
	public String toString() {
		return "! " + operators;
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_state.value = !((Operator) operators).evaluate(curEvent);
		log.debug("eval NOT [{}]", _state.value);
		return _state.value;
	}
}

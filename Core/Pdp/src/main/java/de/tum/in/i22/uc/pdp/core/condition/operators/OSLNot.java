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

	private Operator op;

	public OSLNot() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op = (Operator) operators;

		/*
		 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
		 * At this place, we check whether the operand of NOT(.) is a Literal. If this is not
		 * the case, an IllegalArgumentException is thrown.
		 */
		if (Settings.getInstance().getDistributionEnabled() && !(op instanceof LiteralOperator)) {
			throw new IllegalArgumentException(
					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
							+ this.getClass() + " was not of type " + LiteralOperator.class + ").");
		}

		op.init(mech);
	}

	@Override
	public String toString() {
		return "! " + op;
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_state.value = !op.evaluate(curEvent);
		log.debug("eval NOT [{}]", _state.value);
		return _state.value;
	}
}

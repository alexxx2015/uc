package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType implements LiteralOperator  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public StateBasedOperator() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	int initId(int id) {
		_id = id + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(getClass())
				.add("operator", operator)
				.add("param1", param1)
				.add("param2", param2)
				.add("param3", param3)
				.toString();
	}

	@Override
	public boolean evaluate(IEvent curEvent) {

		IPdp2Pip pip = _pdp.getPip();
		String separator = Settings.getInstance().getSeparator1();

		String p = operator + separator + param1 + separator + param2 + separator + param3;

		boolean result;

		if (curEvent == null) {
			/*
			 * We are evaluating at the end of a timestep
			 */
			result = pip.evaluatePredicateCurrentState(p);
		}
		else {
			/*
			 * We are evaluating in the presence of a given event
			 */
			result = pip.evaluatePredicateSimulatingNextState(curEvent, p);
		}

		if (result) {
			// Notify observers that predicate is true
			setChanged();
			notifyObservers();
		}

		_logger.debug("eval StateBasedOperator [{}: {}]", this, result);

		return result;
	}
}

package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType implements LiteralOperator  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("operator", operator)
				.add("param1", param1)
				.add("param2", param2)
				.add("param3", param3)
				.toString();
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		IPdp2Pip pip = _pdp.getPip();
		String separator = Settings.getInstance().getSeparator1();

		String p = operator + separator + param1 + separator + param2 + separator + param3;

		boolean result;

		if (ev == null) {
			/*
			 * We are evaluating at the end of a timestep
			 */
			result = pip.evaluatePredicateCurrentState(p);
		}
		else {
			/*
			 * We are evaluating in the presence of a given event
			 */
			result = pip.evaluatePredicateSimulatingNextState(ev, p);
		}


		if (result == isPositive()) {
			/*
			 * There are two situations in which this condition may turn true:
			 * a) a 'positive' state based operator's state turned true.
			 * b) a 'non-positive' state based operator's state turned false.
			 *
			 * In both cases we want to signal the state change to our observers.
			 */
			setChanged();
			notifyObservers();
		}

		return result;
	}

	@Override
	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
		boolean result = resultLocalEval;

		if (resultLocalEval != isPositive()) {
			/*
			 * There are two situations in which this condition may turn true:
			 * a) the operator's state is locally satisfied and this does
			 *    _not_ imply global satisfaction (e.g. isNotIn)
			 * b) the operator's state is locally violated and this does
			 *    _not_ imply global violation (e.g. isCombinedWith)
			 *
			 * In both cases we ask the DistributionManager what the remote state
			 * of those operators is.
			 */
			result = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
		}

		return result;
	}

	@Override
	public boolean isPositive() {
		switch (operator) {
			case OP_IS_COMBINED_WITH:
				return true;
			case OP_IS_NOT_IN:
			case OP_IS_ONLY_IN:
				return false;
			default:
				throw new UnsupportedOperationException("Unknown Predicate type: " + operator);
		}
	}
}

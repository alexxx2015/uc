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
		_id = id + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
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


		/* TODO
		 * Alternatively:
		 * if (result == isPositive()) {
		 *   setChanged();
		 *   notifyObservers();
		 * }
		 * Done.
		 */
		if (result && isPositive()) {
			/*
			 * Notify observers that a 'positive' state based operator turned true.
			 */
			setChanged();
			notifyObservers();
		}
		else if (!result && !isPositive()) {
			/*
			 * Notify observers that 'non-positive' state based operators turned false.
			 */
			setChanged();
			notifyObservers();
		}

		return result;
	}

	@Override
	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
		boolean result = resultLocalEval;

		/* TODO
		 * Alternatively:
		 * if (resultLocalEval != isPositive()) {
		 *   ...
		 * }
		 * Done.
		 */
		if (resultLocalEval && !isPositive()) {
			/*
			 * The Operator is locally satisfied. This implies global satisfaction
			 * for 'isCombinedWith' according to CANS'14.
			 * However, for 'isNotIn', it might be the case that the operator is violated
			 * remotely. Thus, we need to ask the DistributionManager.
			 * Note: isNotIn is signaled to the DistributionManager if it is _false_.
			 */
			result = _pdp.getDistributionManager().wasTrueSince(this, _mechanism.getLastUpdate());
		}
		else if (!resultLocalEval && isPositive()) {
			/*
			 * The operator is locally violated. This implies global violation for
			 * 'isNotIn' according to CANS'14.
			 * However, for 'isCombinedWith', it might be the case that the operator
			 * is satisfied remotely. Thus, we need to ask the DistributionManager.
			 */
			result = _pdp.getDistributionManager().wasTrueSince(this, _mechanism.getLastUpdate());
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

package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
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

		if (result) {
			if (operator.equals(OP_IS_COMBINED_WITH)) {
				/*
				 * Notify observers that 'isCombinedWith' turned true.
				 */
				setChanged();
				notifyObservers();
			}
		}
		else {
			if (operator.equals(OP_IS_NOT_IN)) {
				/*
				 * Notify observers that 'isNotIn' turned false.
				 */
				setChanged();
				notifyObservers();
			}
		}

		return result;
	}

	@Override
	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
		boolean result = resultLocalEval;

		if (resultLocalEval) {
			/*
			 * The Operator is locally satisfied. This implies global satisfaction
			 * for 'isCombinedWith' according to CANS'14.
			 * However, for 'isNotIn', it might be the case that the operator is violated
			 * remotely. Thus, we need to ask the DitributionManager.
			 */
			if (operator.equals("isNotIn")) {
				/*
				 * Note: isNotIn is signaled to the DistributionManager if it is _false_.
				 * Therefore, if we get a _positive_ result (i.e. true) from the below query, this means that
				 * isNotIn was _false_. In this case, the overall result is false.
				 */
				if (_pdp.getDistributionManager().wasObservedSince(this, _mechanism.getLastUpdate())) {
					result = false;
				}
				else {
					result = true;
				}
			}
		}
		else {
			/*
			 * The operator is locally violated. This implies global violation for
			 * 'isNotIn' according to CANS'14.
			 * However, for 'isCombinedWith', it might be the case that the operator
			 * is satisfied remotely. Thus, we need to ask the DitributionManager.
			 */
			if (operator.equals("isCombinedWith")) {
				if (_pdp.getDistributionManager().wasObservedSince(this, _mechanism.getLastUpdate())) {
					result = true;
				}
				else {
					result  = false;
				}
			}
		}

		return result;
	}
}

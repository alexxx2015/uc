package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

/**
 * If a StateBasedOperator is satisfied at least once
 * during a timestep, it is considered to be satisfied for the remainder
 * of this timestep as well. This definition is consistent with the one
 * of EventMatchOperator. Also, it allows StateBasedOperators to be used
 * within temporal operators such as Before.
 *
 * Rewritten by Florian Kelbert 2014/11/18.
 *
 * @author Enrico Lovat, Florian Kelbert
 *
 */
public class StateBasedOperator extends StateBasedOperatorType implements AtomicOperator  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	private String _predicate;

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		String sep = Settings.getInstance().getSeparator1();
		_predicate = operator + sep + param1 + sep + param2 + sep + param3;

		/*
		 * I (FK) should be spending some explaining words on _positivity here.
		 * As obvious from the code, isCombinedWith has 'positive' positivity,
		 * while isNotIn and isOnlyIn have negative positivity. This is in
		 * correspondence with the CANS'14 paper, Section 4.2, predicate S.
		 *
		 * isCombinedWith: If this operator is true (i.e. satisfied) within one
		 * system then there is no way this operator might become false (i.e.
		 * violated) when considering additional systems at the same point in
		 * time. This is simply because it is sufficient that the two considered
		 * data items are combined at least once. In other words: if
		 * isCombinedWith is locally satisfied, then it is also globally
		 * satisfied. On the other hand, if isCombinedWith is false (i.e.
		 * violated) locally, then another system might still satisfy the
		 * operator by combining the two considered data items. In summary:
		 * - local satisfaction DOES imply global satisfaction, but
		 * - local violation DOES NOT imply global violation.
		 *
		 * isNotIn/isOnlyIn: If those operators are true (i.e. satisfied) within
		 * one system, then this does not imply that they are true (i.e.
		 * satisfied) globally. Reason: if some data is not within a set of
		 * containers locally, then it might still be the case that at some
		 * remote system the data actually is within this set of containers; the
		 * same arguments apply to isOnlyIn. In other words: local satisfaction
		 * of those operators does not imply their global satisfaction. On the
		 * other hand, if isNotIn is violated locally (i.e. the data actually is
		 * in one of the forbidden containers), then the operator will also
		 * always be violated from a global perspective. In Summary:
		 * - local violation DOES imply global violation, but
		 * - local satisfaction DOES NOT imply global satisfaction.
		 */
		switch (operator) {
		case OP_IS_COMBINED_WITH:
			_positivity = Trilean.TRUE;
			break;
		case OP_IS_NOT_IN:
		case OP_IS_ONLY_IN:
			_positivity = Trilean.FALSE;
			break;
		default:
			throw new UnsupportedOperationException("Unknown Predicate type: " + operator);
		}

		/*
		 * Default values of the operators are the negated value of their
		 * positivity. Reasoning: In each case (i.e. for each operator type,
		 * i.e. with negative or positive positivity), we start with the value
		 * that necessitates coordination with other systems. For
		 * isCombinedWith, this value is false, because local violation does not
		 * imply global violation. For isNotIn/isOnlyIn, this value is true,
		 * because local satisfaction does not imply global satisfaction.
		 */
		_state.set(StateVariable.SINCE_LAST_TICK, !_positivity.value());
		_state.set(StateVariable.VALUE_AT_LAST_TICK, !_positivity.value());
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
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		/*
		 * As of now, our local evaluation result is equal to what has been observed
		 * since the last tick. In case we are NOT ticking at the end of a timestep,
		 * this is our definite result.
		 */
		boolean localResult = sinceLastTick;

		if (endOfTimestep) {
			/*
			 * We are ticking at the end of a timestep. In this case, we need
			 * to evaluate this operator in any case. The reason is that the
			 * current (i.e. at the end of the timestep) evaluation result serves
			 * as the starting value for sinceLastTick for the next timestep.
			 * For example, for isCombinedWith this means that if the two data items
			 * are combined now, then they are also considered to be combined within
			 * the next timestep. The reasoning behind is that the two data items will
			 * not be uncombined before the next event happens. However, the next event
			 * will happen strictly after the end of this timestep, and thus strictly
			 * within the next timestep. For this reason, the isCombinedWith operator
			 * will at least be true until the next event happens. Therefore, it is
			 * true for some time within the next timestep. The sinceLastTick variable
			 * set at the end of this block.
			 */
			boolean valueNow = _pdp.getPip().evaluatePredicateCurrentState(_predicate);

			/*
			 * We check whether the local evaluation result is equal to this
			 * operator's positivity. Only in this case our observers are
			 * notified. Please read the corresponding comment in method
			 * update().
			 */
			if (_positivity.is(valueNow)) {
				localResult = valueNow;
				setChanged();
				notifyObservers(Mechanism.END_OF_TIMESTEP);
			}

			/*
			 * valueAtLastTick: In case the current evaluation was not equal to
			 * this operator's positivity, the value corresponds to the value
			 * of sinceLastTick, i.e. the evaluation that has been accumulated
			 * during the last timestep. In case the current evaluation was equal
			 * to this operator's positivity, the current evaluation value is the result.
			 *
			 * sinceLastTick is set as explained above.
			 */
			_state.set(StateVariable.VALUE_AT_LAST_TICK, localResult);
			_state.set(StateVariable.SINCE_LAST_TICK, valueNow);
		}

		return localResult;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		/*
		 * If sinceLastTick is still not equal to this operator's positivity,
		 * then we need to query remote systems for a definite evaluation result.
		 */
		if (!_positivity.is(sinceLastTick)) {
//			long lastTick = _mechanism.getLastTick();
//			sinceLastTick = _pdp.getDistributionManager().wasNotifiedInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
			sinceLastTick = _pdp.getDistributionManager().wasNotifiedAtTimestep(this, _mechanism.getTimestep());
			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
		}

		return sinceLastTick;
	}

	@Override
	public void update(IEvent ev) {
		/*
		 * An event is happening and updates this operator. We get
		 * the boolean that indicates this operator's value since
		 * the last tick has occurred.
		 */
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		/*
		 * If the sinceLastTick variable is NOT EQUAL to this operator's
		 * positivity, this means that our local evaluation result is not yet
		 * definite: The evaluation so far (within this timestep) always
		 * returned a value that necessitates coordination with other
		 * systems to get a definite evaluation result. Hence, in this case
		 * (i.e. if _positivity != sinceLastTick), we perform an evaluation
		 * of this operator using the PIP (second part of the if statement)
		 * (this execution has been made conditional for performance
		 * reasons). If the evaluation result IS EQUAL to this operator's
		 * positivity, the sinceLastTick state variable is set accordingly
		 * and our observers are notified (which will basically inform
		 * remote systems about our local state change).
		 */
		if (!_positivity.is(sinceLastTick) && _positivity.is(_pdp.getPip().evaluatePredicateCurrentState(_predicate))) {
			_state.set(StateVariable.SINCE_LAST_TICK, _positivity.value());

			/*
			 * Not that our observers are only notified if the result of the
			 * above evaluation corresponds to this operator's positivity.
			 * Consequence: For operators with positive positivity
			 * (isCombinedWith), we notify the observers if the evaluation
			 * result is satisfaction (i.e. telling the others that the
			 * operator was satisfied at our site and hence that it is
			 * satisfied globally). For operators with negative positivity
			 * (isNotIn, isOnlyIn), we notify our observers if the
			 * evaluation result is violation (i.e. telling the others that
			 * the operator was violated at our site and hence that it is
			 * violated globally.
			 */
			setChanged();
			notifyObservers(ev);
		}

		_logger.debug("Updating with event {}. Result: {}.", ev, _state.get(StateVariable.SINCE_LAST_TICK));
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.STATE_BASED;
	}
}

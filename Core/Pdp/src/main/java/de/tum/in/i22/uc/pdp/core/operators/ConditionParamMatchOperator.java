package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.ParamMatch;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType implements AtomicOperator {
	private static Logger _logger = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

	private ParamMatch pm;

	public ConditionParamMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		pm = new ParamMatch();
		pm.setName(getName());
		pm.setValue(getValue());
		pm.setCmpOp(getCmpOp());

		_state.set(StateVariable.SINCE_UPDATE, false);
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("name", name)
				.add("value", value)
				.add("cmpOp", cmpOp)
				.toString();
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		/*
		 * Lookup whether the last event parameter matched.
		 */
		boolean value = _state.get(StateVariable.SINCE_UPDATE);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, value);
		_state.set(StateVariable.SINCE_UPDATE, false);
		return value;
	}

	@Override
	public void update(IEvent ev) {
		if (pm.matches(pm.getName(), ev.getParameterValue(pm.getName()))) {
			_state.set(StateVariable.SINCE_UPDATE, true);
		}

		_logger.debug("Updating with event {}. Result: {}.", ev, _state.get(StateVariable.SINCE_UPDATE));
	}

	@Override
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.CONDITION_PARAM_MATCH;
	}

	@Override
	public boolean isAtomic() {
		return true;
	}

	@Override
	public boolean isDNF() {
		return true;
	}

	@Override
	protected void setRelevant(boolean relevant) {
		_state.set(StateVariable.RELEVANT, relevant);
	}
}

package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.ParamMatch;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType implements Observer {
	private static Logger _logger = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

	private ParamMatch pm;

	public ConditionParamMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		pm = new ParamMatch();
		pm.setName(getName());
		pm.setValue(getValue());
		pm.setCmpOp(getCmpOp());
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
	public boolean tick() {
		/*
		 * Lookup whether the last event parameter matched.
		 */
		boolean value = _state.get(StateVariable.SINCE_LAST_TICK);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, value);
		return value;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			boolean matches = pm.matches(pm.getName(), ((IEvent) arg).getParameterValue(pm.getName()));

			/*
			 * Always overwrite SINCE_LAST_TICK, because we are only
			 * interested in the last event happening, i.e. the event
			 * happening NOW.
			 */
			_state.set(StateVariable.SINCE_LAST_TICK, matches);

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
		}
	}

	@Override
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}
}

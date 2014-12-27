package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.NotType;

public class OSLNot extends NotType {
	private static Logger _logger = LoggerFactory.getLogger(OSLNot.class);

	private Operator op;

	public OSLNot() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		op = (Operator) operators;
		op.init(mech, this, ttl);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		_positivity = op.getPositivity().negate();
	}

	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * At this place, we check whether the operand of NOT(.) is a Literal. If this is not
	 * the case, an InvalidOperatorException is thrown.
	 *
	 * @throws InvalidOperatorException if the operand is not a {@link AtomicOperator}.
	 */
	private void ensureDNF() throws InvalidOperatorException {
		if (!(op.isAtomic())) {
			throw new InvalidOperatorException(
					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
							+ getClass() + " was not of type " + AtomicOperator.class + ").");
		}
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "!(" + op + ")";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		boolean valueAtLastTick = !op.tick(endOfTimestep);

		_logger.info("op: {}. Result: {}", !valueAtLastTick, valueAtLastTick);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		boolean valueAtLastTick = !op.distributedTickPostprocessing(endOfTimestep);

		_logger.info("op: {}. Result: {}", !valueAtLastTick, valueAtLastTick);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
	}

	@Override
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		op.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OSL_NOT;
	}

	@Override
	public boolean isDNF() {
		try {
			ensureDNF();
			return true;
		}
		catch (InvalidOperatorException e) {
			return false;
		}
	}

	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}

	@Override
	protected void setRelevant(boolean relevant) {
		_state.set(StateVariable.RELEVANT, relevant);
		op.setRelevant(relevant);
	}
}

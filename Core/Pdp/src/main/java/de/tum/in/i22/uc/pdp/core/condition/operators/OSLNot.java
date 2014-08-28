package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.NotType;

public class OSLNot extends NotType {
	private static Logger _logger = LoggerFactory.getLogger(OSLNot.class);

	private Operator op;

	public OSLNot() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op = (Operator) operators;

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		op.init(mech);
	}

	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * At this place, we check whether the operand of NOT(.) is a Literal. If this is not
	 * the case, an IllegalStateException is thrown.
	 *
	 * @throws IllegalStateException if the operand is not a {@link LiteralOperator}.
	 */
	private void ensureDNF() {
		if (!(op instanceof LiteralOperator)) {
			throw new IllegalStateException(
					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
							+ getClass() + " was not of type " + LiteralOperator.class + ").");
		}
	}

	@Override
	int initId(int id) {
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "! " + op;
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		boolean newStateValue = !op.evaluate(curEvent);

		if (newStateValue != _state.value) {
			_state.value = newStateValue;
			setChanged();
			notifyObservers(_state);
		}

		_logger.debug("eval NOT [{}]", _state.value);
		return _state.value;
	}
}

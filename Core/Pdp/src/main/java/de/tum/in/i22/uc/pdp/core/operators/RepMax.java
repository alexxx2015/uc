package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepMaxType;

public class RepMax extends RepMaxType {
	private static Logger _logger = LoggerFactory.getLogger(RepMax.class);
	private Operator op;

	private boolean _immutable = false;
	private long _stateCounter = 0;

	private final Deque<Boolean> _backupImmutable;
	private final Deque<Long> _backupStateCounter;

	public RepMax() {
		_backupImmutable = new ArrayDeque<>(2);
		_backupStateCounter = new ArrayDeque<>(2);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		op = (Operator) operators;
		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "REPMAX(" + getLimit() + "," + op + ")";
	}

	@Override
	public boolean tick() {
		if (_immutable) {
			return false;
		}
		else {
			if (op.tick()) {
				_stateCounter++;
				_logger.debug("Subformula was true. Incrementing counter to {}.", _stateCounter);
			}

			_valueAtLastTick = (_stateCounter <= limit);

			if (!_valueAtLastTick) {
				_logger.debug("[REPMAX] Activating immutability");
				_immutable = true;
			}
		}
		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
		_backupImmutable.addFirst(_immutable);
		_backupStateCounter.addFirst(_stateCounter);
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
		_immutable = _backupImmutable.getFirst();
		_stateCounter = _backupStateCounter.getFirst();
	}
}

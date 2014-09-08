package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger _logger = LoggerFactory.getLogger(Since.class);

	private Operator op1;
	private Operator op2;

	private boolean _alwaysASinceLastB = false;
	private boolean _alwaysA = true;

	private final Deque<Boolean> _backupAlwaysASinceLastB;
	private final Deque<Boolean> _backupAlwaysA;

	public Since() {
		_backupAlwaysASinceLastB = new ArrayDeque<>(2);
		_backupAlwaysA = new ArrayDeque<>(2);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		setId(op1.initId(id) + 1);
		return op2.initId(getId());
	}

	@Override
	public String toString() {
		return "SINCE (" + op1 + ", " + op2 + " )";
	}

	@Override
	public boolean tick() {
		// A since B
		boolean stateA = op1.tick();
		boolean stateB = op2.tick();

		if (!stateA) {
			_alwaysA = false;
		}

		if (_alwaysA) {
			_valueAtLastTick = true;
			_logger.debug("A was always true. Result: {}.", _valueAtLastTick);
		}
		else {
			if (stateB) {
				_valueAtLastTick = true;
				_alwaysASinceLastB = true;
				_logger.debug("B is happening at this timestep. Result: {}.", _valueAtLastTick);
			}
			else {
				if (stateA && _alwaysASinceLastB) {
					_valueAtLastTick = true;
					_logger.debug("A was always true since last B happened. Result: {}.", _valueAtLastTick);
				}
				else {
					_valueAtLastTick = false;
					_alwaysASinceLastB = false;
					_logger.debug("A was NOT always true since last B happened. Result: {}.", _valueAtLastTick);
				}
			}
		}

		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op1.startSimulation();
		op2.startSimulation();
		_backupAlwaysA.addFirst(_alwaysA);
		_backupAlwaysASinceLastB.addFirst(_alwaysASinceLastB);
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op1.stopSimulation();
		op2.stopSimulation();
		_alwaysA = _backupAlwaysA.getFirst();
		_alwaysASinceLastB = _backupAlwaysASinceLastB.getFirst();
	}
}

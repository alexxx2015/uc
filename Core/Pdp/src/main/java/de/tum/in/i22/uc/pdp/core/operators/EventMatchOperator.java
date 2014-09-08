package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class EventMatchOperator extends EventMatch implements LiteralOperator, Observer {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	private boolean _didHappenSinceLastTick = false;

	private final Deque<Boolean> _backupDidHappenSinceLastTick;

	public EventMatchOperator() {
		_backupDidHappenSinceLastTick = new ArrayDeque<>(2);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		_pdp.addObserver(this);
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public boolean tick() {
		_valueAtLastTick = _didHappenSinceLastTick;
		_didHappenSinceLastTick = false;

		if (!_valueAtLastTick && Settings.getInstance().getDistributionEnabled()) {
			/*
			 * Last resort: The event might have happened remotely
			 */
			_valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
		}

		return _valueAtLastTick;
	}

	@Override
	public boolean isPositive() {
		return true;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		_backupDidHappenSinceLastTick.addFirst(_didHappenSinceLastTick);
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		_didHappenSinceLastTick = _backupDidHappenSinceLastTick.getFirst();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {
			if (!_didHappenSinceLastTick) {
				_didHappenSinceLastTick = matches((IEvent) arg);

				if (_didHappenSinceLastTick) {
					setChanged();
					notifyObservers();
				}
			}
			_logger.debug("Updating with event {}. Result: {}.", arg, _didHappenSinceLastTick);
		}
	}
}

package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.OccurMinEventType;

public class OccurMinEvent extends OccurMinEventType implements AtomicOperator, Observer {
	private static Logger _logger = LoggerFactory.getLogger(OccurMinEvent.class);

	private EventMatchOperator event;
	private TimeAmount _timeAmount;

	public OccurMinEvent() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive.");
		}

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		if (_timeAmount.getTimestepInterval() <= 0) {
			throw new IllegalStateException("Arguments must result in a positive timestepValue.");
		}

		CircularArray<Long> stateCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			stateCircArray.set(0L, a);
		}

		_state.set(StateVariable.COUNT_AT_LAST_TICK, 0L);
		_state.set(StateVariable.CIRC_ARRAY, stateCircArray);

		event = (EventMatchOperator) getEvent();
		event.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));

		// get notified whenever the underlying event occurs
		event.addObserver(this);

		// How often the event has happened since the last tick
		_state.set(StateVariable.COUNTER, 0L);
		_positivity = Trilean.TRUE;
	}

	@Override
	protected int initId(int id) {
		return setId(event.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "OccurMinEvent(" + limit + "," + event + "," + _timeAmount + ")";
	}

	@Override
	public boolean tick() {
		/*
		 *  tick() the underlying event such that it adjusts its state
		 *  according to its tick() semantics.
		 *  However, we are not interested in its result. We maintain the
		 *  corresponding state (i.e. how often the event happened) ourselves.
		 *  We can do this in a separate Thread that runs
		 *  in parallel to what we are doing.
		 */
		Thread tickThread = new Thread(new EventTickThread());
		tickThread.start();

		/*
		 * Get saved state variables.
		 */
		CircularArray<Long> stateCircArray = _state.get(StateVariable.CIRC_ARRAY);
		long countAtLastTick = _state.get(StateVariable.COUNT_AT_LAST_TICK);

		_logger.debug("Counter: {}, old CircArray: {}.", _state.get(StateVariable.COUNTER), _state.get(StateVariable.CIRC_ARRAY));

		/*
		 * Do the actual work.
		 */

		countAtLastTick -= stateCircArray.pop(); 						// Delete oldest value from array and subtract it from count
		countAtLastTick += (long) _state.get(StateVariable.COUNTER);	// Add the accumulated (during last timestep) counter value to the count

		stateCircArray.push((long) _state.get(StateVariable.COUNTER));	// Push the counter value to the array

		boolean valueAtLastTick = (countAtLastTick >= limit);

		_logger.debug("Result: {} (count: {}, limit: {}, new CircArray: {}.).", valueAtLastTick, countAtLastTick, limit, _state.get(StateVariable.CIRC_ARRAY));

		/*
		 * Save/reset state variables.
		 */
		_state.set(StateVariable.COUNTER, 0L);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		_state.set(StateVariable.COUNT_AT_LAST_TICK, countAtLastTick);

		waitFor(tickThread);
		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing() {
		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (!valueAtLastTick) {
			// Lookup how often the event happened globally. The result will include our local count.
			long globallyTrue = _pdp.getDistributionManager().howOftenTrueInBetween(event, _mechanism.getLastTick(), _mechanism.getLastTick() + _mechanism.getTimestepSize());

			/*
			 * Get saved state variables.
			 */
			CircularArray<Long> stateCircArray = _state.get(StateVariable.CIRC_ARRAY);
			long countAtLastTick = _state.get(StateVariable.COUNT_AT_LAST_TICK);

			/*
			 * Do the actual work.
			 */

			countAtLastTick -= stateCircArray.removeLast(); 	// Subtract the value that has been counted locally and remove it from CircularArray
			countAtLastTick += globallyTrue;					// Add the value that has been counted globally

			stateCircArray.push(globallyTrue);				// Push the globally counted value to CircularArray

			valueAtLastTick = (countAtLastTick >= limit);

			_logger.debug("Distributed result: {} (count: {}, limit: {}, new CircArray: {}.).", valueAtLastTick, countAtLastTick, limit, _state.get(StateVariable.CIRC_ARRAY));

			/*
			 * Save state variables.
			 */
			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
			_state.set(StateVariable.COUNT_AT_LAST_TICK, countAtLastTick);
		}

		return valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		event.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		event.stopSimulation();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof EventMatchOperator) {
			// Our event has happened.
			_state.set(StateVariable.COUNTER, ((long) _state.get(StateVariable.COUNTER)) + 1);
			_logger.debug("Event happened: [{}]; Counter: {}.", o, (long) _state.get(StateVariable.COUNTER));
		}
	}

//	private class HowOftenTrueGloballyThread implements Runnable {
//		private long _from;
//		private long _to;
//		private CircularArray<Long> _array;
//
//		public HowOftenTrueGloballyThread(long from, long to, CircularArray<Long> array) {
//			_from = from;
//			_to = to;
//			_array = array;
//		}
//
//		@Override
//		public void run() {
//			_array.push(_pdp.getDistributionManager().howOftenTrueInBetween(event, _from, _to));
//		}
//	}

	/**
	 * A thread that issues tick() on the underlying {@link EventMatchOperator}.
	 */
	private class EventTickThread implements Runnable {
		@Override
		public void run() {
			event.tick();
		}
	}

	/**
	 * Waits for the specified {@link Thread}.
	 * @param t the thread to wait for.
	 */
	private void waitFor(Thread t) {
		try {
			t.join();
		} catch (InterruptedException e) {
			_logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		event.getObservers(observers);
		return observers;
	}
}

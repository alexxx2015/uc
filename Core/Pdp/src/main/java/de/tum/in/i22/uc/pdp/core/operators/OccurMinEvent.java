package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
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

public class OccurMinEvent extends OccurMinEventType implements AtomicOperator {
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

		CircularArray<Integer> stateCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			stateCircArray.set(0, a);
		}

		_state.set(StateVariable.COUNT_AT_LAST_TICK, 0);
		_state.set(StateVariable.CIRC_ARRAY, stateCircArray);

		event = (EventMatchOperator) getEvent();
		event.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));

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
	public boolean tick(boolean endOfTimestep) {
		/*
		 *  tick() the underlying event such that it adjusts its state
		 *  according to its tick() semantics.
		 */
		event.tick(endOfTimestep);

		/*
		 * Get saved state variables.
		 */
		CircularArray<Integer> stateCircArray = _state.get(StateVariable.CIRC_ARRAY);
		int countAtLastTick = _state.get(StateVariable.COUNT_AT_LAST_TICK);

		_logger.debug("Counter: {}, old CircArray: {}.", event.getValueAtLastTick(), _state.get(StateVariable.CIRC_ARRAY));

		/*
		 * Do the actual work.
		 */

		countAtLastTick -= stateCircArray.pop(); 						// Delete oldest value from array and subtract it from count
		countAtLastTick += event.getCountAtLastTick();					// Add the accumulated (during last timestep) counter value to the count

		stateCircArray.push(event.getCountAtLastTick());				// Push the counter value to the array

		boolean result = (countAtLastTick >= limit);

		_logger.debug("Result: {} (count: {}, limit: {}, new CircArray: {}.).", result, countAtLastTick, limit, _state.get(StateVariable.CIRC_ARRAY));

		/*
		 * Save/reset state variables.
		 */
		_state.set(StateVariable.COUNT_AT_LAST_TICK, countAtLastTick);

		return result;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		boolean result = (int) _state.get(StateVariable.COUNT_AT_LAST_TICK) >= limit;

		if (!result) {
			/*
			 *  Our local count was not sufficient. Look up how
			 *  often the event happened globally.
			 *  The result will include our local counts, except
			 *  the one event occurrence that might have happened
			 *  at this point in time (i.e. if an event is occurring).
			 *  For this reason, if the event has happened since the last
			 *  update (getSinceUpdate()), we add one to compensate for this.
			 */
			int globallyTrue = _pdp.getDistributionManager().howOftenNotifiedInBetween(event, _mechanism.getLastTick(), _mechanism.getLastTick() + _mechanism.getTimestepSize());

			// TODO double check. Also the comment above.
//			if (event.getSinceUpdate()) {
//				globallyTrue++;
//			}

			/*
			 * Get saved state variables.
			 */
			CircularArray<Integer> stateCircArray = _state.get(StateVariable.CIRC_ARRAY);
			int countAtLastTick = _state.get(StateVariable.COUNT_AT_LAST_TICK);

			/*
			 * Do the actual work.
			 */

			countAtLastTick -= stateCircArray.removeLast(); 	// Subtract the value that has been counted locally and remove it from CircularArray
			countAtLastTick += globallyTrue;					// Add the value that has been counted globally

			stateCircArray.push(globallyTrue);					// Push the globally counted value to CircularArray

			result = (countAtLastTick >= limit);

			_logger.debug("Distributed result: {} (count: {}, limit: {}, new CircArray: {}.).", result, countAtLastTick, limit, _state.get(StateVariable.CIRC_ARRAY));

			/*
			 * Save state variables.
			 */
			_state.set(StateVariable.COUNT_AT_LAST_TICK, countAtLastTick);
		}

		return result;
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

//	@Override
//	public void update(Observable o, Object arg) {
//		if (o instanceof EventMatchOperator) {
//			// Our event has happened.
//			_state.set(StateVariable.COUNTER, ((long) _state.get(StateVariable.COUNTER)) + 1);
//			_logger.debug("Event happened: [{}]; Counter: {}.", o, (long) _state.get(StateVariable.COUNTER));
//		}
//	}

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

//	/**
//	 * A thread that issues tick() on the underlying {@link EventMatchOperator}.
//	 */
//	private class EventTickThread implements Runnable {
//		@Override
//		public void run() {
//			event.tick();
//		}
//	}

//	/**
//	 * Waits for the specified {@link Thread}.
//	 * @param t the thread to wait for.
//	 */
//	private void waitFor(Thread t) {
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			_logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//	}

	@Override
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		event.getObservers(observers);
		return observers;
	}
}

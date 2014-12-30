package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.OccurMinEventType;

public class OccurMinEvent extends OccurMinEventType {
	private static Logger _logger = LoggerFactory.getLogger(OccurMinEvent.class);

	private EventMatchOperator event;
	private TimeAmount _timeAmount;

	public OccurMinEvent() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());

		event = (EventMatchOperator) getEvent();
		event.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));

		CircularArray<Integer> stateCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			stateCircArray.set(0, a);
		}

		_state.set(StateVariable.COUNT_AT_LAST_TICK, 0);
		_state.set(StateVariable.CIRC_ARRAY, stateCircArray);

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
			 * CIRC_ARRAY counts locally only!
			 * If this result is not sufficient, ask remotely!
			 */
			return limit <= _pdp.getDistributionManager().howOftenNotifiedSinceTimestep(event,
								_mechanism.getTimestep() - _timeAmount.getTimestepInterval());
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

	@Override
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		event.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OCCUR_MIN_EVENT;
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
		super.setRelevant(relevant);
		event.setRelevant(relevant);
	}
}

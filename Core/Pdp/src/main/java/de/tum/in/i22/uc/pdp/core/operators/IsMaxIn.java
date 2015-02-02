package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.factories.MessageFactory;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.ComparisonOperatorTypes;
import de.tum.in.i22.uc.pdp.xsd.IsMaxInType;

public class IsMaxIn extends IsMaxInType implements AtomicOperator {
	private static Logger _logger = LoggerFactory.getLogger(IsMaxIn.class);

	private DataBasic _data; // Use DataBasic, because JAXB does not support interfaces (i.e. IData)
	private Set<Pair<IName,ComparisonOperatorTypes>> _containerNames;
	private long _limit;

	public IsMaxIn() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		_data = new DataBasic(getDataId());
		_limit = getLimit();

		getContainer().forEach(c -> _containerNames.add(Pair.of(MessageFactory.createName(c.getName()), c.getCmpOp())));

		_state.set(StateVariable.SINCE_LAST_TICK, true);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, true);

		_positivity = Trilean.FALSE;
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public String toString() {
		return "IsMaxIn(" + _data + ", " + _limit + ", " + _containerNames + ")";
	}
//
//	@Override
//	public boolean tick(boolean endOfTimestep) {
//		/*
//		 *  tick() the underlying event such that it adjusts its state
//		 *  according to its tick() semantics.
//		 */
//		event.tick(endOfTimestep);
//
//		/*
//		 * Get saved state variables.
//		 */
//		CircularArray<Integer> stateCircArray = _state.get(StateVariable.CIRC_ARRAY);
//		int countAtLastTick = _state.get(StateVariable.COUNT_AT_LAST_TICK);
//
//		_logger.debug("Counter: {}, old CircArray: {}.", event.getValueAtLastTick(), _state.get(StateVariable.CIRC_ARRAY));
//
//		/*
//		 * Do the actual work.
//		 */
//
//		countAtLastTick -= stateCircArray.pop(); 						// Delete oldest value from array and subtract it from count
//		countAtLastTick += event.getCountAtLastTick();					// Add the accumulated (during last timestep) counter value to the count
//
//		stateCircArray.push(event.getCountAtLastTick());				// Push the counter value to the array
//
//		boolean result = (countAtLastTick >= limit);
//
//		_logger.debug("Result: {} (count: {}, limit: {}, new CircArray: {}.).", result, countAtLastTick, limit, _state.get(StateVariable.CIRC_ARRAY));
//
//		/*
//		 * Save/reset state variables.
//		 */
//		_state.set(StateVariable.COUNT_AT_LAST_TICK, countAtLastTick);
//
//		return result;
//	}
//
//	@Override
//	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
//		boolean result = (int) _state.get(StateVariable.COUNT_AT_LAST_TICK) >= limit;
//
//		if (!result) {
//			/*
//			 * CIRC_ARRAY counts locally only!
//			 * If this result is not sufficient, ask remotely!
//			 */
//			return limit <= _pdp.getDistributionManager().howOftenNotifiedSinceTimestep(event,
//								_mechanism.getTimestep() - _timeAmount.getTimestepInterval());
//		}
//
//		return result;
//	}
//
	@Override
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.IS_MAX_IN;
	}

	@Override
	public boolean isAtomic() {
		return true;
	}
//
//	@Override
//	public boolean isDNF() {
//		return true;
//	}

	@Override
	public void update(IEvent event) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void update(IEvent event) {
//		if (!(boolean) _state.get(StateVariable.RELEVANT)) {
//			return;
//		}
//
//		if ((boolean) _state.get(StateVariable.SINCE_LAST_TICK)) {
//			int countainerCount = 0;
//			for (IContainer cont : _pdp.getPip().getContainersForData(_data)) {
//				// TODO: Rather: Get all container names for this data. Then compare for each returned IName,
//				// whether it matches one of _containerNames. If so, containerCount++ .
//
//				// Compile error left on purpose
//
//			}
//		}
//	}
}

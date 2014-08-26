package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	int initId(int id) {
		_id = id + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_logger.debug("eval TRUE");
		return true;
	}
}

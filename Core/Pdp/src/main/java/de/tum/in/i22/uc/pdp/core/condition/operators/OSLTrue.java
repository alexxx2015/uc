package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType implements LiteralOperator {
	private static Logger log = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		log.debug("eval TRUE");
		return true;
	}
}

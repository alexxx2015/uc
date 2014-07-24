package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType {
	private static Logger log = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
	}

	public OSLTrue(TrueType op, Mechanism curMechanism) {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean evaluate(Event curEvent) {
		log.debug("eval TRUE");
		return true;
	}
}

package de.tum.in.i22.uc.pip.core.eventdef;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class DefaultEventHandler extends BaseEventHandler {

	public DefaultEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return DummyMessageGen.createOkStatus();
	}
}

package de.tum.in.i22.pip.core.eventdef;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class DefaultActionHandler extends BaseEventHandler {

	public DefaultActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return DummyMessageGen.createOkStatus();
	}
}

package de.tum.in.i22.pip.core.actions;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class DefaultActionHandler extends BaseActionHandler {

	public DefaultActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return DummyMessageGen.createOkStatus();
	}
}

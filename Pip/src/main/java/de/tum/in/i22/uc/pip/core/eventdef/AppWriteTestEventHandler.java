package de.tum.in.i22.uc.pip.core.eventdef;

import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class AppWriteTestEventHandler extends GenericAppEventHandler {

	public AppWriteTestEventHandler() {
		super();
	}

	@Override
	public String scopeName(String delimiter) {
		return "AppWriteTestEventHandler (" + delimiter + ")";
	}

	@Override
	public IStatus execute() {
		return null;
	}
}

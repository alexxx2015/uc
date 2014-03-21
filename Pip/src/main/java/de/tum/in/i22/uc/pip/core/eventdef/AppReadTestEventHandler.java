package de.tum.in.i22.uc.pip.core.eventdef;

import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class AppReadTestEventHandler extends GenericAppEventHandler {

	public AppReadTestEventHandler() {
		super();
	}

	@Override
	public String scopeName(String delimiter) {
		return "AppReadTestEventHandler (" + delimiter + ")";
	}

	@Override
	public IStatus execute() {
		return null;
	}
}

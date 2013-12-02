package de.tum.in.i22.pip.core.eventdef;

public class AppReadTestEventHandler extends GenericAppEventHandler {

	public AppReadTestEventHandler() {
		super();
	}

	@Override
	public String scopeName(String delimiter) {
		return "AppReadTestEventHandler (" + delimiter + ")";
	}
}

package de.tum.in.i22.pip.core.eventdef;

public class AppWriteTestEventHandler extends GenericAppEventHandler {

	public AppWriteTestEventHandler() {
		super();
	}

	@Override
	public String scopeName(String delimiter) {
		return "AppWriteTestEventHandler (" + delimiter + ")";
	}
}

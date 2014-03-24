package de.tum.in.i22.uc.pip.core.eventdef;


public class ParameterNotFoundException extends Exception {
	private static final long serialVersionUID = -669044665756388548L;

	public ParameterNotFoundException(String parameter) {
		super("Parameter '" + parameter + "' not found.");
	}
}

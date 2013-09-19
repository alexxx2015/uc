package de.tum.in.i22.pip.core.eventdef;


public class ParameterNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParameterNotFoundException(String parameter) {
		super("Parameter '" + parameter + "' not found.");
	}
}

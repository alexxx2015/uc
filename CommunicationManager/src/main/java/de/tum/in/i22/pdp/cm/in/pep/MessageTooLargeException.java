package de.tum.in.i22.pdp.cm.in.pep;

public class MessageTooLargeException extends Exception {
	private static final long serialVersionUID = 4123982926639672251L;

	public MessageTooLargeException(String m) {
		super(m);
	}
}

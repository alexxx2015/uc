package de.tum.in.i22.cm.out;

public class NotConnectedException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotConnectedException(String m) {
		super(m);
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
}

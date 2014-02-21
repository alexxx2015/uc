package de.tum.in.i22.pip.core;

/**
 * A container can have several representations.
 * PipName is a representation of the container.
 * @author Stoimenov
 *
 */
public class Name {
	public String _name;

	public Name(String name) {
		this._name = name;
	}

	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return _name;
	}
}

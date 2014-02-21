package de.tum.in.i22.pip.core;

/**
 * A container can have several representations.
 * PipName is a representation of the container.
 * @author Stoimenov
 *
 */
public class Name {
	private final String _name ;

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

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return _name.equals(obj);
	}
}

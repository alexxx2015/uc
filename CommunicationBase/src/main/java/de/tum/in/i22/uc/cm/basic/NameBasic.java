package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.IName;

/**
 * A container can have several representations.
 * PipName is a representation of the container.
 * @author Stoimenov
 *
 */
public class NameBasic implements IName {
	private final String _name ;

	public NameBasic(String name) {
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

package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

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

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return _name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NameBasic) {
			return Objects.equals(_name, ((NameBasic) obj)._name);
		}
		return false;
	}
}

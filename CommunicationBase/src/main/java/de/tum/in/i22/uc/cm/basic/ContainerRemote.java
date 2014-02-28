package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.distr.ILocation;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class ContainerRemote implements IContainer {
	private final NameBasic _name;
	private final ILocation _location;

	public ContainerRemote(NameBasic name, ILocation location) {
		_name = name;
		_location = location;
	}

	public NameBasic getName() {
		return _name;
	}

	public ILocation getLocation() {
		return _location;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getClassValue() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContainerRemote) {
			ContainerRemote o = (ContainerRemote) obj;
			return Objects.equals(_name, o._name)
					&& Objects.equals(_location, o._location);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _location);
	}
}

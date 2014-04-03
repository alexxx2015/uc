package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.distribution.Location;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteSocketContainer extends SocketContainer {
	/**
	 * The location of this remote container
	 */
	private final Location _location;

	/**
	 * Its unique remote name, for convenience.
	 */
	private final SocketName _socketName;


	public RemoteSocketContainer(SocketName socketName, Domain domain, Type type, Location location) {
		super(domain, type);
		_location = location;
		_socketName = socketName;
	}

	public Location getLocation() {
		return _location;
	}

	public SocketName getSocketName() {
		return _socketName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteSocketContainer) {
			RemoteSocketContainer o = (RemoteSocketContainer) obj;
			return super.equals(obj)
					&& Objects.equals(_location, o._location)
					&& Objects.equals(_socketName, o._socketName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.getId(), super.getClass(), _location, _socketName);
	}

}

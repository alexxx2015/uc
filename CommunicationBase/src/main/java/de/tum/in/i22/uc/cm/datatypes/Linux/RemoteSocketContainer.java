package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.out.Connector;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteSocketContainer extends SocketContainer {
	/**
	 * How we can connect to the remote location
	 */
	private final Connector _connector;

	/**
	 * Its unique remote name, for convenience.
	 */
	private final SocketName _socketName;


	public RemoteSocketContainer(SocketName socketName, Domain domain, Type type, Connector connector) {
		super(domain, type);
		_connector = connector;
		_socketName = socketName;
	}

	public Connector getConnector() {
		return _connector;
	}

	public SocketName getSocketName() {
		return _socketName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteSocketContainer) {
			RemoteSocketContainer o = (RemoteSocketContainer) obj;
			return super.equals(obj)
					&& Objects.equals(_connector, o._connector)
					&& Objects.equals(_socketName, o._socketName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.getId(), super.getClass(), _connector, _socketName);
	}

}

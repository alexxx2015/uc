package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.out.Connection;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteSocketContainer extends SocketContainer {
	private final Connection _connection;

	public RemoteSocketContainer(Domain domain, Type type, Connection connection) {
		super(domain, type);
		_connection = connection;
	}

	public Connection getConnection() {
		return _connection;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteSocketContainer) {
			RemoteSocketContainer o = (RemoteSocketContainer) obj;
			return super.equals(obj)
					&& Objects.equals(_connection, o._connection);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.getId(), super.getClass(), _connection);
	}

}

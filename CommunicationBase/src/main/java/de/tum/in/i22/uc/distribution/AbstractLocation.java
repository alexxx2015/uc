package de.tum.in.i22.uc.distribution;

public abstract class AbstractLocation implements ILocation {
	private final IConnection _connection;

	public AbstractLocation(IConnection connection) {
		_connection = connection;
	}


	@Override
	public IConnection getConnection() {
		return _connection;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}

package de.tum.in.i22.uc.cm.datatypes.linux;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * An internal identifier, e.g.:
 * socket:[xyz]
 * pipe:[xyz]
 *
 * @author Florian Kelbert
 *
 */
public class OSInternalName extends NameBasic {

	static final String PREFIX_INTERN = " INT_";

	private final String _host;
	private final String _identifier;

	private OSInternalName(String host, String identifier, String name) {
		super(name);

		_host = host;
		_identifier = identifier;
	}

	public static OSInternalName create(String host, String identifier) {
		return new OSInternalName(host, identifier, PREFIX_INTERN + host + "_" + identifier);
	}

	public String getHost() {
		return _host;
	}

	public String getIdentifier() {
		return _identifier;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_host", _host)
				.add("_identifier", _identifier)
				.toString();
	}
}

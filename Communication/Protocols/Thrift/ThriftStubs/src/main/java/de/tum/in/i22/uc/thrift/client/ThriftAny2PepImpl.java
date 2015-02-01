package de.tum.in.i22.uc.thrift.client;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pep;
import de.tum.in.i22.uc.thrift.types.TAny2Pep;

class ThriftAny2PepImpl implements IAny2Pep {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PepImpl.class);

	private final TAny2Pep.Client _handle;

	public ThriftAny2PepImpl(TAny2Pep.Client handle) {
		_handle = handle;
	}

	@Override
	public IPLocation getResponsiblePdpLocation() {
		String loc;
		try {
			loc = _handle.getResponsiblePdpLocation();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		if (loc.equals(LocalLocation.local)) {
			return IPLocation.localIpLocation;
		}
		else {
			return new IPLocation(loc);
		}
	}
}

package de.tum.in.i22.uc.thrift.server;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pep;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pep;

class TAny2PepThriftServer extends ThriftServerHandler implements TAny2Pep.Iface {

	private final IAny2Pep _handler;

	public TAny2PepThriftServer(IAny2Pep handler) {
		_handler = handler;
	}

	@Override
	public String getResponsiblePdpLocation() throws TException {
		return ThriftConverter.toThrift(_handler.getResponsiblePdpLocation());
	}
}
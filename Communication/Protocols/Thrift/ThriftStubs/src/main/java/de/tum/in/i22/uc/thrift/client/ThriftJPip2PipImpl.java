package de.tum.in.i22.uc.thrift.client;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IJPip2Pip;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TJPip2Pip;

class ThriftJPip2PipImpl implements IJPip2Pip {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftJPip2PipImpl.class);

	private final TJPip2Pip.Client _handle;

	public ThriftJPip2PipImpl(TJPip2Pip.Client handle) {
		_handle = handle;
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		try {
			return ThriftConverter.fromThrift(_handle.addJPIPListener(ip, port, id, filter));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		try {
			return ThriftConverter.fromThrift(_handle.setUpdateFrequency(msec, id));
		} catch (TException e) {
			e.printStackTrace();
		}		return null;
	}
}

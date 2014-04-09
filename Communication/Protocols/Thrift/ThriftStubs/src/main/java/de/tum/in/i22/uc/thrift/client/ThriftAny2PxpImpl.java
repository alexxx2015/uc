package de.tum.in.i22.uc.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pxp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;

class ThriftAny2PxpImpl implements IAny2Pxp {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PdpClient.class);

	private final TAny2Pxp.Client _handle;

	public ThriftAny2PxpImpl(TAny2Pxp.Client handle) {
		_handle = handle;
	}

	@Override
	public IStatus executeSync(List<IEvent> event) {
		try {
			return ThriftConverter.fromThrift(
					_handle.executeSync(
							ThriftConverter.toThriftEventList(
									event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void executeAsync(List<IEvent> event) {
		try {
			_handle.executeAsync(ThriftConverter.toThriftEventList(event));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}

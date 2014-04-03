package de.tum.in.i22.uc.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pxp;
import de.tum.in.i22.uc.cm.client.PxpClientHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.thrift.ThriftConverter;


/**
 * The client side of a remote Thrift {@link PxpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PxpClientHandler#connect()}) and
 * do calls on a remote {@link PxpProcessor}.
 *
 * @author Enrico  Lovat
 *
 */
class ThriftPxpClientHandler extends PxpClientHandler<TAny2Pxp.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPxpClientHandler.class);

	/**
	 * Creates a {@link ThriftPxpClientHandler} that will be
	 * connected (upon calling {@link PxpClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	ThriftPxpClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pxp.Client.class));
	}

	ThriftPxpClientHandler(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	@Override
	public IStatus execute(List<IEvent> event) {
		try {
			return ThriftConverter.fromThrift(_handle.execute(ThriftConverter.toThriftEventList(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}

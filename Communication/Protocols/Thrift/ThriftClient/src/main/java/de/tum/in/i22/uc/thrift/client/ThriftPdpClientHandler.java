package de.tum.in.i22.uc.thrift.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConverter;


/**
 * The client side of a remote Thrift {@link PdpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PdpClientHandler#connect()}) and
 * do calls on a remote {@link PdpProcessor}.
 *
 * Use {@link ThriftClientHandlerFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
class ThriftPdpClientHandler extends PdpClientHandler<TAny2Pdp.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPdpClientHandler.class);

	/**
	 * Creates a {@link ThriftPdpClientHandler} that will be
	 * connected (upon calling {@link PdpClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	ThriftPdpClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftPdpClientHandler} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientHandlerFactory} to get an instance.
	 *
	 * @param location the location of the remote point
	 */
	ThriftPdpClientHandler(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<IMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IResponse notifyEventAsync(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		try {
			return ThriftConverter.fromThrift(
					_handle.
					notifyEventSync(
							ThriftConverter.
							toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}

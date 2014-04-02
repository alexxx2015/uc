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
import de.tum.in.i22.uc.cm.pdp.core.IPdpMechanism;
import de.tum.in.i22.uc.thrift.ThriftTypes;


/**
 *
 * @author Florian Kelbert
 *
 */
public class ThriftPdpClientHandler extends PdpClientHandler<TAny2Pdp.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPdpClientHandler.class);

	public ThriftPdpClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
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
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms() {
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
			return ThriftTypes.fromThrift(
					_handle.
					notifyEventSync(
							ThriftTypes.
							toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}

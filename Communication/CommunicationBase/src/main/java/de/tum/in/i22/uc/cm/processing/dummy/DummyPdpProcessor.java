/**
 *
 *
 *  THIS
 *
 *  CLASS
 *
 *  IS
 *
 *  FOR
 *
 *  TESTING
 *
 *  PURPOSES
 *
 *  ONLY
 *
 *
 */

package de.tum.in.i22.uc.cm.processing.dummy;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;

public class DummyPdpProcessor extends PdpProcessor implements IDummyProcessor {
	public DummyPdpProcessor() {
		super(LocalLocation.getInstance());
	}

	private static Logger _logger = LoggerFactory.getLogger(DummyPdpProcessor.class);

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("exportMechanism method invoked");
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("revokePolicy method invoked");
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("revokeMechanism method invoked");
		return null;
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("deployPolicyURI method invoked");
		return null;
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("deployPolicyXML method invoked");
		return null;
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("listMechanisms method invoked");
		return null;
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("registerPxp method invoked");
		return false;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("notifyEventAsync method invoked");

	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		// TODO Auto-generated method stub
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("notifyEventSync method invoked");
		return null;
	}

}

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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
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
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("revokePolicy method invoked");
		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("revokeMechanism method invoked");
		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("deployPolicyXML method invoked");
		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("listMechanisms method invoked");
		return Collections.emptyMap();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("registerPxp method invoked");
		return false;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("notifyEventAsync method invoked");
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("notifyEventSync method invoked");
		return new ResponseBasic(new StatusBasic(EStatus.ALLOW));
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("processEventAsync method invoked");
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("processEventSync method invoked");
		return new ResponseBasic(new StatusBasic(EStatus.ALLOW));
	}

	@Override
	public boolean reset() {
		_logger.error("PdpProcessor DUMMY Implementation");
		_logger.error("reset method invoked");
		return true;
	}
}

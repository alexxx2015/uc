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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DummyPmpProcessor extends PmpProcessor implements IDummyProcessor {
	public DummyPmpProcessor() {
		super(LocalLocation.getInstance());
	}

	private static Logger _logger = LoggerFactory.getLogger(DummyPmpProcessor.class);

	@Override
	public IMechanism exportMechanismPmp(String par) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("exportMechanism method invoked");
		return null;
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("revokePolicy method invoked");
		return null;
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("revokeMechanism method invoked");
		return null;
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("deployPolicyURI method invoked");
		return null;
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("deployPolicyXML method invoked");
		return null;
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("listMechanisms method invoked");
		return null;
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("deployPolicyRawXML method invoked");
		return null;
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		// TODO Auto-generated method stub
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("getPolicies method invoked");
		return null;
	}

	@Override
	public IPtpResponse translatePolicy(String requestId,
			Map<String, String> parameters, XmlPolicy xmlPolicy) {
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("translatePolicy method invoked");
		return null;
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,
			Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("updateDomainModel method invoked");
		return null;
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("listPoliciesPmp method invoked");
		return null;
	}

	@Override
	public IStatus incomingPolicyTransfer(XmlPolicy xml, String from) {
		_logger.error("PmpProcessor DUMMY Implementation");
		_logger.error("remotePolicyTransfer method invoked");
		return null;
	}

}

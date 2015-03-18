package de.tum.in.i22.uc.thrift.server;

import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TPtpResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;

/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PmpThriftServer extends ThriftServerHandler implements
TAny2Pmp.Iface {

	private final IAny2Pmp _handler;

	TAny2PmpThriftServer(IAny2Pmp handler) {
		_handler = handler;
	}

	@Override
	public TStatus revokeMechanismPmp(String policyName, String mechName)
			throws TException {
		IStatus status = _handler.revokeMechanismPmp(policyName, mechName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyURIPmp(String policyFilePath) throws TException {
		IStatus status = _handler.deployPolicyURIPmp(policyFilePath);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyXMLPmp(TXmlPolicy XMLPolicy) throws TException {
		IStatus status = _handler.deployPolicyXMLPmp(ThriftConverter
				.fromThrift(XMLPolicy));
		return ThriftConverter.toThrift(status);
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() throws TException {
		return _handler.listMechanismsPmp();
	}

	@Override
	public TStatus revokePolicyPmp(String policyName) throws TException {
		IStatus status = _handler.revokePolicyPmp(policyName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyRawXMLPmp(String xml) throws TException {
		IStatus status = _handler.deployPolicyRawXMLPmp(xml);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public Set<TXmlPolicy> getPolicies(TData data) throws TException {
		Set<XmlPolicy> policies = _handler.getPolicies(ThriftConverter.fromThrift(data));
		return ThriftConverter.toThriftPoliciesSet(policies);
	}

	@Override
	public TPtpResponse translatePolicy(String requestId, Map<String, String> parameters, TXmlPolicy xmlPolicy) throws TException {
		IPtpResponse response = _handler.translatePolicy(requestId, parameters, ThriftConverter.fromThrift(xmlPolicy));
		return ThriftConverter.toThrift(response);
	}

	@Override
	public TPtpResponse updateDomainModel(String requestId, Map<String, String> parameters, TXmlPolicy xmlDomainModel)
			throws TException {
		return ThriftConverter.toThrift(_handler.updateDomainModel(requestId, parameters, ThriftConverter.fromThrift(xmlDomainModel)));
	}

	@Override
	public Set<TXmlPolicy> listPoliciesPmp() throws TException {
		Set<XmlPolicy> policies = _handler.listPoliciesPmp();
		return ThriftConverter.toThriftPoliciesSet(policies);
	}

	@Override
	public TStatus remotePolicyTransfer(TXmlPolicy xml, String from) throws TException {
		return ThriftConverter.toThrift(_handler.remotePolicyTransfer(ThriftConverter.fromThrift(xml), from));
	}
}

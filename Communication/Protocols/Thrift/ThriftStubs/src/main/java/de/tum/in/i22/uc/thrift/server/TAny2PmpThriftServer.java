package de.tum.in.i22.uc.thrift.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
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
	private static Logger _logger = LoggerFactory
			.getLogger(TAny2PmpThriftServer.class);

	private final IAny2Pmp _handler;

	TAny2PmpThriftServer(IAny2Pmp handler) {
		_handler = handler;
	}

	@Override
	public TStatus informRemoteDataFlow(String srcAddress, int srcPort,
			String dstAddress, int dstPort, Set<TData> data) throws TException {
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		IStatus status = _handler.informRemoteDataFlow(new IPLocation(
				srcAddress, srcPort), new IPLocation(dstAddress, dstPort), d);
		return ThriftConverter.toThrift(status);
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
	public Map<String, List<String>> listMechanismsPmp() throws TException {
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
	public TStatus specifyPolicyFor(Set<TContainer> representations,
			String dataClass) throws TException {
		Set<IContainer> representationsI = new HashSet<IContainer>();
		for (TContainer cont : representations)
			representationsI.add(ThriftConverter.fromThrift(cont));
		return ThriftConverter.toThrift(_handler.specifyPolicyFor(
				representationsI, dataClass));
	}
}

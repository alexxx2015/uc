package de.tum.in.i22.uc.thrift.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PmpThriftServer extends ThriftServerHandler implements TAny2Pmp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PmpThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2PmpThriftServer(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TStatus remotePolicyTransfer(Set<String> policies) throws TException {
		_logger.debug("TAny2Pmp: remotePolicyTransfer");
		IStatus result = _requestHandler.receivePolicies(policies);
		return ThriftConverter.toThrift(result);
	}

	@Override
	public TStatus informRemoteDataFlow(String srcAddress, int srcPort, String dstAddress, int dstPort, Set<TData> data) throws TException {
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		IStatus status = _requestHandler.informRemoteDataFlow(new IPLocation(srcAddress, srcPort), new IPLocation(dstAddress, dstPort), d);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus revokeMechanismPmp(String policyName, String mechName)
			throws TException {
		IStatus status = _requestHandler.revokeMechanismPmp(policyName, mechName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyURIPmp(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		IStatus status = _requestHandler.deployPolicyURIPmp(policyFilePath);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyXMLPmp(String XMLPolicy) throws TException {
		// TODO Auto-generated method stub
		IStatus status = _requestHandler.deployPolicyXMLPmp(XMLPolicy);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() throws TException {
		return _requestHandler.listMechanismsPmp();
	}

	@Override
	public TStatus revokePolicyPmp(String policyName) throws TException {
		// TODO Auto-generated method stub
		IStatus status = _requestHandler.revokePolicy(policyName);
		return ThriftConverter.toThrift(status);
	}
}

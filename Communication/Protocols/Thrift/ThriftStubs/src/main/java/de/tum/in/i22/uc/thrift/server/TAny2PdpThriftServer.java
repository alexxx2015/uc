package de.tum.in.i22.uc.thrift.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Enrico Lovat & Florian Kelbert
 *
 */
class TAny2PdpThriftServer extends ThriftServerHandler implements TAny2Pdp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PdpThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2PdpThriftServer(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TResponse notifyEventSync(TEvent e) throws TException {
		_logger.debug("TAny2Pdp: notifyEventSync");
		IEvent ev = ThriftConverter.fromThrift(e);
		IResponse r = _requestHandler.notifyEventSync(ev);
		return ThriftConverter.toThrift(r);
	}

	@Override
	public void notifyEventAsync(TEvent e) throws TException {
		//identical to sync version, but discards the response
		_logger.debug("TAny2Pdp: notifyEventAsync");
		IEvent ev = ThriftConverter.fromThrift(e);
		_requestHandler.notifyEventAsync(ev);
	}


	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		_logger.debug("TAny2Pdp: registerPxp");
		return _requestHandler.registerPxp(ThriftConverter.fromThrift(pxp));
	}

	@Override
	public TStatus revokePolicy(String policyName) throws TException {
		_logger.debug("TAny2Pdp: revokePolicy");
		IStatus status = _requestHandler.revokePolicyPmp(policyName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus revokeMechanism(String policyName, String mechName) throws TException {
		_logger.debug("TAny2Pdp: revokeMechanism");
		IStatus status = _requestHandler.revokeMechanismPmp(policyName, mechName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyURI(String policyFilePath) throws TException {
		_logger.debug("TAny2Pdp: deployPolicy");
		IStatus status = _requestHandler.deployPolicyURIPmp(policyFilePath);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyXML(String XMLPolicy) throws TException {
		_logger.debug("TAny2Pdp: deployPolicy");
		IStatus status = _requestHandler.deployPolicyXMLPmp(XMLPolicy);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		_logger.debug("TAny2Pdp: listMech");
		return _requestHandler.listMechanismsPmp();
	}

}

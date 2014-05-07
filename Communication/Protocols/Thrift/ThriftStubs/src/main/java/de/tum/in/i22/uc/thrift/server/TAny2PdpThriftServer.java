package de.tum.in.i22.uc.thrift.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
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
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;
import de.tum.in.i22.uc.thrift.types.TobiasEvent;
import de.tum.in.i22.uc.thrift.types.TobiasResponse;
import de.tum.in.i22.uc.thrift.types.TobiasStatusType;


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
	public Map<String, List<String>> listMechanisms() throws TException {
		_logger.debug("TAny2Pdp: listMech");
		return _requestHandler.listMechanismsPmp();
	}

	@Override
	public TStatus deployPolicyXML(TXmlPolicy XMLPolicy) throws TException {
		_logger.debug("TAny2Pdp: deployPolicy");
		IStatus status = _requestHandler.deployPolicyXMLPmp(ThriftConverter.fromThrift(XMLPolicy));
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TobiasResponse processEventSync(TobiasEvent e, String senderID)
			throws TException {
		_logger.debug("TAny2Pdp: processEventSync");
		Map<String,String> map = new HashMap<String,String>(e.getParameters()); 
		map.put("senderID", senderID);
		IEvent ev = new EventBasic(e.getName(), map, false);
		IResponse res = _requestHandler.processEventSync(ev);
		EStatus st=null;
		TobiasStatusType resStatus = null;
		if (res!=null){
			st= res.getAuthorizationAction().getEStatus();
			switch (st){
			case OKAY:
				resStatus=TobiasStatusType.OK;
				break;
			case ERROR:
				resStatus=TobiasStatusType.ERROR;
				break;
			case INHIBIT:
				resStatus=TobiasStatusType.INHIBIT;
				break;
			case ALLOW:
				resStatus=TobiasStatusType.ALLOW;
				break;
			case MODIFY:
				resStatus=TobiasStatusType.MODIFY;
				break;
			case ERROR_EVENT_PARAMETER_MISSING:
			case REMOTE_DATA_FLOW_HAPPENED:
			default:
				resStatus=TobiasStatusType.ERROR;
				break;
			}
		} else {
			resStatus=TobiasStatusType.ERROR;
		}
		return new TobiasResponse(resStatus);
	}

	@Override
	public void processEventAsync(TobiasEvent e, String senderID)
			throws TException {
		_logger.debug("TAny2Pdp: processEventAsync");
		Map<String,String> map = new HashMap<String,String>(e.getParameters()); 
		map.put("senderID", senderID);
		IEvent ev = new EventBasic(e.getName(), map, false);
		_requestHandler.processEventAsync(ev);
	}

}

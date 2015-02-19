package de.tum.in.i22.uc.thrift.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
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
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TobiasEvent;
import de.tum.in.i22.uc.thrift.types.TobiasResponse;
import de.tum.in.i22.uc.thrift.types.TobiasStatusType;

class ThriftAny2PdpImpl implements IAny2Pdp {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PdpImpl.class);

	private final TAny2Pdp.Client _handle;

	public ThriftAny2PdpImpl(TAny2Pdp.Client handle) {
		_handle = handle;
	}


	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Method not yet supported
		_logger.error("exportMechanism method not yet supported");
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		_logger.debug("revoke policy (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.revokePolicy(policyName));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		_logger.debug("revoke mechanism (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.revokeMechanism(policyName, mechName));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		_logger.debug("deploy policy (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyXML(ThriftConverter.toThrift(XMLPolicy)));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		_logger.debug("listMechanisms (Pdp client)");
		try {
			return _handle.listMechanisms();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		_logger.debug("registerPxp (Pdp client)");
		boolean b =false;
		try {
			b=_handle.registerPxp(ThriftConverter.toThrift(pxp));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_logger.debug("notify event async (Pdp client)");
		try{
			_handle.notifyEventAsync(ThriftConverter.toThrift(event));
		} catch (TException e){
			e.printStackTrace();
		}
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		_logger.debug("notify event sync (Pdp client)");
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


	@Override
	public void processEventAsync(IEvent pepEvent) {
		_logger.debug("TobiasProcessEventAsync (Pdp client)");
		Map<String,String> map = new HashMap<String,String>(pepEvent.getParameters());
		map.remove("senderID");
		TobiasEvent ev = new TobiasEvent(pepEvent.getName(), map, pepEvent.getTimestamp());
		try {
			_handle.processEventAsync(ev,
					pepEvent.getParameters().get("senderID"));
		} catch (TException e) {
			_logger.error("TobiasProcessEventAsync failed. Exception follows:");
			e.printStackTrace();
		}

		}


	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		_logger.debug("TobiasProcessEventSync (Pdp client)");
		Map<String,String> map = new HashMap<String,String>(pepEvent.getParameters());
		map.remove("senderID");
		TobiasEvent ev = new TobiasEvent(pepEvent.getName(), map, pepEvent.getTimestamp());
		TobiasResponse tr = null;
		try {
			tr = _handle.processEventSync(ev,
					pepEvent.getParameters().get("senderID"));
		} catch (TException e) {
			_logger.error("TobiasProcessEventAsync failed. Exception follows:");
			e.printStackTrace();
		}
		if (tr==null) return new ResponseBasic(new StatusBasic(EStatus.ERROR, "Error! Didn't manage to execute TobiasProcessEventSync"), null, null);
		TobiasStatusType st= tr.getStatus();
		EStatus resStatus= null;
		switch (st){
		case OK:
			resStatus=EStatus.OKAY;
			break;
		case ERROR:
			resStatus=EStatus.ERROR;
			break;
		case INHIBIT:
			resStatus=EStatus.INHIBIT;
			break;
		case ALLOW:
			resStatus=EStatus.ALLOW;
			break;
		case MODIFY:
			resStatus=EStatus.MODIFY;
			break;
		default:
			resStatus=EStatus.ERROR;
			break;
		}
		return new ResponseBasic(new StatusBasic(resStatus), null, null);
	}


	@Override
	public boolean reset() {
		_logger.debug("reset (Pdp client)");
		try {
			return _handle.reset();
		} catch (TException e) {
			e.printStackTrace();
		}
		return false;
	}
}

package de.tum.in.i22.uc.thrift.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.PtpResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TContainer;

class ThriftAny2PmpImpl implements IAny2Pmp {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PmpImpl.class);

	private final TAny2Pmp.Client _handle;

	public ThriftAny2PmpImpl(TAny2Pmp.Client handle) {
		_handle = handle;
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation, Location dstLocation, Set<IData> data) {
		throw new RuntimeException("informRemoteDataFlow not implemented");
	}


	@Override
	public IMechanism exportMechanismPmp(String par) {
		throw new RuntimeException("exportMechanismPmp not implemented");
	}


	@Override
	public IStatus revokePolicyPmp(String policyName) {
		try {
			return ThriftConverter.fromThrift(_handle.revokePolicyPmp(policyName));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}


	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		try {
			return ThriftConverter.fromThrift(_handle.revokeMechanismPmp(policyName, mechName));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyURIPmp(policyFilePath));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyXMLPmp(ThriftConverter.toThrift(XMLPolicy)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		try {
			return _handle.listMechanismsPmp();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyRawXMLPmp(xml));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		try {
			return ThriftConverter.fromThriftPolicySet(_handle.getPolicies(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		Set<TContainer> representationsT = new HashSet<TContainer>();
		for (IContainer cont : representations) representationsT.add(ThriftConverter.toThrift(cont));
		try {
			return ThriftConverter.fromThrift(_handle.specifyPolicyFor(representationsT, dataClass));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IPtpResponse translatePolicy(String requestId,Map<String, String> parameters, XmlPolicy xmlPolicy) {
		if((requestId == null) || parameters==null || xmlPolicy==null){
			IPtpResponse response = new PtpResponseBasic(new StatusBasic(EStatus.ERROR), new XmlPolicy("invalid param", ""));
			return response;
		}
		
		try {
			return ThriftConverter.fromThrift(_handle.translatePolicy(requestId, parameters, ThriftConverter.toThrift(xmlPolicy)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		if((requestId == null) || parameters==null || xmlDomainModel==null){
			IPtpResponse response = new PtpResponseBasic(new StatusBasic(EStatus.ERROR), new XmlPolicy("invalid param", ""));
			return response;
		}
		
		try {
			return ThriftConverter.fromThrift(_handle.updateDomainModel(requestId, parameters, ThriftConverter.toThrift(xmlDomainModel)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}

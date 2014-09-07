package de.tum.in.i22.uc.thrift.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TPtpResponse;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;
import de.tum.in.i22.uc.thrift.types.TobiasEvent;
import de.tum.in.i22.uc.thrift.types.TobiasResponse;

/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Enrico Lovat & Florian Kelbert
 *
 */
class TAny2AnyThriftServer extends ThriftServerHandler implements
TAny2Any.Iface {

	private TAny2Pdp.Iface _pdpServer;
	private TAny2Pip.Iface _pipServer;
	private TAny2Pmp.Iface _pmpServer;

	TAny2AnyThriftServer(int pdpPort, int pipPort, int pmpPort) {
		try {
			_pdpServer = new ThriftConnector<>("localhost", pdpPort,
					TAny2Pdp.Client.class).connect();
			_pipServer = new ThriftConnector<>("localhost", pipPort,
					TAny2Pip.Client.class).connect();
			_pmpServer = new ThriftConnector<>("localhost", pmpPort,
					TAny2Pmp.Client.class).connect();
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize "
					+ TAny2AnyThriftServer.class.getSimpleName() + ".");
		}
	}

	@Override
	public TResponse notifyEventSync(TEvent e) throws TException {
		return _pdpServer.notifyEventSync(e);
	}

	@Override
	public void notifyEventAsync(TEvent e) throws TException {
		_pdpServer.notifyEventAsync(e);
	}

	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		return _pdpServer.registerPxp(pxp);
	}

	@Override
	public TStatus revokePolicy(String policyName) throws TException {
		return _pdpServer.revokePolicy(policyName);
	}

	@Override
	public TStatus revokeMechanism(String policyName, String mechName)
			throws TException {
		return _pdpServer.revokeMechanism(policyName, mechName);
	}

	@Override
	public TStatus deployPolicyURI(String policyFilePath) throws TException {
		return _pdpServer.deployPolicyURI(policyFilePath);
	}

	@Override
	public Map<String, Set<String>> listMechanisms() throws TException {
		return _pdpServer.listMechanisms();
	}

	@Override
	public TStatus initialRepresentation(TName container, Set<TData> data)
			throws TException {
		return _pipServer.initialRepresentation(container, data);
	}

	@Override
	public TData newInitialRepresentation(TName container) throws TException {
		return _pipServer.newInitialRepresentation(container);
	}

	@Override
	public boolean hasAllData(Set<TData> data) throws TException {
		return _pipServer.hasAllData(data);
	}

	@Override
	public boolean hasAnyData(Set<TData> data) throws TException {
		return _pipServer.hasAnyData(data);
	}

	@Override
	public boolean hasAllContainers(Set<TName> names) throws TException {
		return _pipServer.hasAllContainers(names);
	}

	@Override
	public boolean hasAnyContainer(Set<TName> names) throws TException {
		return _pipServer.hasAnyContainer(names);
	}

	@Override
	public TStatus update(TEvent event) throws TException {
		return _pipServer.update(event);
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event,
			String predicate) throws TException {
		return _pipServer
				.evaluatePredicateSimulatingNextState(event, predicate);
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		return _pipServer.evaluatePredicatCurrentState(predicate);
	}

	@Override
	public Set<TContainer> getContainerForData(TData data) throws TException {
		return _pipServer.getContainerForData(data);
	}

	@Override
	public Set<TData> getDataInContainer(TName containerName) throws TException {
		return _pipServer.getDataInContainer(containerName);
	}

	@Override
	public TStatus startSimulation() throws TException {
		return _pipServer.startSimulation();
	}

	@Override
	public TStatus stopSimulation() throws TException {
		return _pipServer.stopSimulation();
	}

	@Override
	public boolean isSimulating() throws TException {
		return _pipServer.isSimulating();
	}

	@Override
	public void executeAsync(List<TEvent> eventList) throws TException {
		// TODO Auto-generated method stub

	}

	@Override
	public TStatus executeSync(List<TEvent> eventList) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> whoHasData(Set<TData> data, int recursionDepth)
			throws TException {
		return _pipServer.whoHasData(data, recursionDepth);
	}

	@Override
	public TStatus revokePolicyPmp(String policyName) throws TException {
		return _pdpServer.revokePolicy(policyName);
	}

	@Override
	public TStatus revokeMechanismPmp(String policyName, String mechName)
			throws TException {
		return _pdpServer.revokeMechanism(policyName, mechName);
	}

	@Override
	public TStatus deployPolicyURIPmp(String policyFilePath) throws TException {
		return _pdpServer.deployPolicyURI(policyFilePath);
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() throws TException {
		return _pdpServer.listMechanisms();
	}

	@Override
	public TData newStructuredData(Map<String, Set<TData>> structure)
			throws TException {
		return _pipServer.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<TData>> getStructureOf(TData data) throws TException {
		return _pipServer.getStructureOf(data);
	}

	@Override
	public Set<TData> flattenStructure(TData data) throws TException {
		return _pipServer.flattenStructure(data);
	}

	@Override
	public TStatus deployPolicyXML(TXmlPolicy XMLPolicy) throws TException {
		return _pdpServer.deployPolicyXML(XMLPolicy);
	}

	@Override
	public TStatus deployPolicyRawXMLPmp(String xml) throws TException {
		return _pmpServer.deployPolicyRawXMLPmp(xml);
	}

	@Override
	public TStatus deployPolicyXMLPmp(TXmlPolicy XMLPolicy) throws TException {
		return _pmpServer.deployPolicyXMLPmp(XMLPolicy);
	}

	@Override
	public Set<TXmlPolicy> getPolicies(TData data) throws TException {
		return _pmpServer.getPolicies(data);
	}

	@Override
	public TobiasResponse processEventSync(TobiasEvent e, String senderID)
			throws TException {
		return _pdpServer.processEventSync(e, senderID);
	}

	@Override
	public void processEventAsync(TobiasEvent e, String senderID)
			throws TException {
		_pdpServer.processEventAsync(e, senderID);
	}

	@Override
	public TStatus specifyPolicyFor(Set<TContainer> representations,
			String dataClass) throws TException {
		return _pmpServer.specifyPolicyFor(representations, dataClass);
	}

	@Override
	public TData getDataFromId(String id) throws TException {
		return _pipServer.getDataFromId(id);
	}

	@Override
	public TPtpResponse translatePolicy(String requestId,
			Map<String, String> parameters, TXmlPolicy xmlPolicy)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TPtpResponse updateDomainModel(String requestId,
			Map<String, String> parameters, TXmlPolicy xmlDomainModel)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TStatus informRemoteDataFlow(String srcAddress, int srcPort,
			String dstAddress, int dstPort, Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}

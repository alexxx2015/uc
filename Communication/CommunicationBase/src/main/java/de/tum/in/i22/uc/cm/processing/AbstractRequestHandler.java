package de.tum.in.i22.uc.cm.processing;

import java.io.File;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;

public abstract class AbstractRequestHandler implements IRequestHandler {

	@Override
	public void notifyEventAsync(IEvent pepEvent) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IResponse notifyEventSync(IEvent pepEvent) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IMechanism exportMechanism(String par) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent eventToSimulate,
			String predicate) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus startSimulation() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus stopSimulation() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean isSimulating() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus update(IEvent updateEvent) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution conflictResolutionFlag) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IData getDataFromId(String id) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IChecksum getChecksumOf(IData data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean deleteChecksum(IData d) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean deleteStructure(IData d) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IPtpResponse translatePolicy(String requestId,
			Map<String, String> parameters, XmlPolicy xmlPolicy) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,
			Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost,
			IName containerName, Set<IData> data) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName,
			long firstTick) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator,
			long timestep) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void doDataTransfer(RemoteDataFlowInfo dataflow) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void register(XmlPolicy policy) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void deregister(String policyName, IPLocation location) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public String getIfModel() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public boolean reset() {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException("Method not implemented.");
	}
}

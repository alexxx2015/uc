package de.tum.in.i22.uc.cm.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Any;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp;

public class Pmp2AnyClient implements IPmp2Any {

	protected IPmp2Pdp _pdp;
	protected IPmp2Pip _pip;
	protected IPmp2Pmp _pmp;

	public Pmp2AnyClient() {
	}

	public Pmp2AnyClient(IPmp2Pdp pdp, IPmp2Pip pip, IPmp2Pmp pmp) {
		_pdp = pdp;
		_pip = pip;
		_pmp = pmp;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		return _pdp.exportMechanism(par);
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		return _pdp.revokePolicy(policyName);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		return _pdp.revokeMechanism(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		return _pdp.deployPolicyURI(policyFilePath);
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		return _pdp.deployPolicyXML(XMLPolicy);
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		return _pdp.listMechanisms();
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		return _pip.initialRepresentation(containerName, data);
	}

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		return _pmp.receivePolicies(policies);
	}
}

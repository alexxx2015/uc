package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PMP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Pdp")
public interface IPmp2Pdp {
	@AThriftMethod(signature="// TODO")
	public IMechanism exportMechanism(String par);

	@AThriftMethod(signature="Types.TStatus revokePolicy (1: string policyName)")
	public IStatus revokePolicy(String policyName);

	@AThriftMethod(signature="Types.TStatus revokeMechanism (1: string policyName, 2: string mechName)")
	public IStatus revokeMechanism(String policyName, String mechName);

	@AThriftMethod(signature="Types.TStatus deployPolicyXML (1: Types.TXmlPolicy XMLPolicy)")
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy);

	@AThriftMethod(signature="map<string,set<string>> listMechanisms()")
	public Map<String, Set<String>> listMechanisms();
}

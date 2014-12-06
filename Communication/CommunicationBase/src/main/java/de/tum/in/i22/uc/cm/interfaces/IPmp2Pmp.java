package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PMP can invoke on a PMP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPmp2Pmp")
public interface IPmp2Pmp extends IPmp2Ptp{
	/**
	 *
	 * Same methods that can be found in the PDP.
	 * In this case the PMP acts as Man-in-the-middle.
	 *
	 * @param par
	 * @return
	 */

	@AThriftMethod(signature="// TODO")
	public IMechanism exportMechanismPmp(String par);

	@AThriftMethod(signature="Types.TStatus revokePolicyPmp (1: string policyName)")
	public IStatus revokePolicyPmp(String policyName);

	@AThriftMethod(signature="Types.TStatus revokeMechanismPmp (1: string policyName, 2: string mechName)")
	public IStatus revokeMechanismPmp(String policyName, String mechName);

	@AThriftMethod(signature="Types.TStatus deployPolicyURIPmp (1: string policyFilePath)")
	public IStatus deployPolicyURIPmp(String policyFilePath);

	@AThriftMethod(signature="Types.TStatus deployPolicyXMLPmp (1: Types.TXmlPolicy XMLPolicy)")
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy);

	@AThriftMethod(signature="map<string,set<string>> listMechanismsPmp()")
	public Map<String, Set<String>> listMechanismsPmp();

	@AThriftMethod(signature="set<Types.TXmlPolicy> listPoliciesPmp()")
	public Set<XmlPolicy> listPoliciesPmp();

	@AThriftMethod(signature="Types.TStatus deployPolicyRawXMLPmp(1: string xml)")
	public IStatus deployPolicyRawXMLPmp(String xml);

	@AThriftMethod(signature="set<Types.TXmlPolicy> getPolicies(1: Types.TData data)")
	public Set<XmlPolicy> getPolicies(IData data);

	@AThriftMethod(signature="Types.TStatus remotePolicyTransfer (1: string xml, 2: string from)")
	public IStatus remotePolicyTransfer(String xml, String from);

}

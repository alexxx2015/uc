package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface IPmp2Pmp {
	/**
	 * Transfers the specified policies to this PMP, which
	 * will then 'manage' them and deploy them at the PDP.
	 *
	 * @param policies the transferred policies
	 * @return
	 */
	@AThriftMethod(signature="Types.TStatus remotePolicyTransfer(1: set<string> policies)")
	public IStatus receivePolicies(Set<String> policies);
	
	
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

	@AThriftMethod(signature="Types.TStatus deployPolicyXMLPmp (1: string XMLPolicy)")
	public IStatus deployPolicyXMLPmp(String XMLPolicy);

	@AThriftMethod(signature="map<string,list<string>> listMechanismsPmp()")
	public Map<String, List<String>> listMechanismsPmp();
	
}
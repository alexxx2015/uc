package de.tum.in.i22.policyeditor.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.model.UserClass;
import de.tum.in.i22.policyeditor.model.UserObject;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PdpClient;

public class DeploymentController {

	private static final Logger _logger = LoggerFactory.getLogger(DeploymentController.class);
		
	private Any2PmpClient clientPmp;
	
	public DeploymentController(Any2PmpClient clientPmp2) {
		this.clientPmp = clientPmp2;
	}

	public List<PolicyTemplate> getDeployedPolicies(String dataClass){
		List<PolicyTemplate> deployedPolicies = new ArrayList<PolicyTemplate>();
		
		Map<String, Set<String>> mechanisms = clientPmp.listMechanismsPmp();
		
		for(String key : mechanisms.keySet()){
			Set<String> mechanism = mechanisms.get(key);
			_logger.debug(key);
			_logger.debug(mechanism.toString());
		}
		
				
		return deployedPolicies;
	}
	
	public void closeConnection(){
		clientPmp.disconnect();
	}
	
	/**
	 * For all the data representations of the data,
	 * deploy all the policies.
	 * @param representations
	 * @param dataClass
	 * @param policies
	 */
	public void deployPolicies(Set<IContainer> representations, String dataClass, List<PolicyTemplate> policies){
		String message = "policies translated: "+ policies.size();
		_logger.info(message);
		
		UserObject container = new UserObject("dummyObject");
		for(IContainer representation : representations){
			String containerName = representation.getId();
			container = new UserObject(containerName);	
		}
		
		UserClass dataClassObj = new UserClass(dataClass);
		int policyInstanceId = 0;
		for (PolicyTemplate policy : policies) {
			
			policy.instantiatePolicyClass();
			policy.instantiatePolicyObject(container);
			
			_logger.info(policy.toStringExtended());
									
			Map<String, String> parameters = new HashMap<>();
			parameters.put("policy_template", policy.getTemplate());
			parameters.put("policy_class", policy.getDataClass());
			parameters.put("policy_description", policy.getDescription());
			parameters.put("template_id", policy.getId());
			
			String policyName = "p"+policy.getId();
			String xmlRepresentation = policy.getInstance();
			XmlPolicy xmlPolicy = new XmlPolicy(policyName, xmlRepresentation);
			
			String requestId = policy.getId() +"_"+policyInstanceId;
			IPtpResponse response = clientPmp.translatePolicy(requestId, parameters, xmlPolicy);
			String msg = "policy: " + response.getStatus().getEStatus() +" "+ requestId + " "+ policy.getClearDescription();
			_logger.info(msg);
			policyInstanceId ++;
		}
		
		this.getDeployedPolicies(dataClass);
		
	}
	
}

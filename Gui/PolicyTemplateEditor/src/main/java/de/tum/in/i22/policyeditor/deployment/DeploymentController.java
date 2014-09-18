package de.tum.in.i22.policyeditor.deployment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.model.UserObject;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;

public class DeploymentController {

	private static final Logger _logger = LoggerFactory.getLogger(DeploymentController.class);
		
	private Any2PmpClient clientPmp;

	private static int globalPolicyCounter = 0;
	
	public DeploymentController(Any2PmpClient clientPmp2) {
		this.clientPmp = clientPmp2;
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
		
		//UserClass dataClassObj = new UserClass(dataClass);
		for (PolicyTemplate policy : policies) {
			
			policy.instantiatePolicyClass();
			policy.instantiatePolicyObject(container);
			
			//_logger.info(policy.toStringExtended());
									
			Map<String, String> parameters = new HashMap<>();
			
			String policyName = policy.getName();
			if(policyName.equals("")){
				//generate a policy name
				Calendar cal = Calendar.getInstance();
				String timestamp = cal.get(Calendar.YEAR)+""+cal.get(Calendar.MONTH)+""+cal.get(Calendar.DAY_OF_MONTH)
						+"-"+ cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)+""+cal.get(Calendar.SECOND)+""+globalPolicyCounter++; 
				policyName = timestamp+"#"+policy.getTemplateId();
			}
			String xmlRepresentation = policy.getInstance();
			String pDescription = policy.getDescription();
			String pTemplateId = policy.getTemplateId();
			String pTemplate = policy.getTemplate();
			String pDataClass = policy.getDataClass();
			
			
			XmlPolicy xmlPolicy = new XmlPolicy(policyName, xmlRepresentation);
			xmlPolicy.setDescription(pDescription);
			xmlPolicy.setTemplateId(pTemplateId);
			xmlPolicy.setTemplateXml(pTemplate);
			xmlPolicy.setDataClass(pDataClass);
			
			String requestId = policyName;
			_logger.debug("translate and deploy: "+ policyName +"\n"+xmlRepresentation);
			System.err.println(xmlRepresentation);
			IPtpResponse response = clientPmp.translatePolicy(requestId, parameters, xmlPolicy);
			String msg = "policy: " + response.getStatus().getEStatus() +" "+ requestId + " "+ policy.getClearDescription();
			_logger.info(msg);
		}
		
		
	}
	
	public List<PolicyTemplate>  getDeployedPolicies(String dataClass){
		List<PolicyTemplate> deployedPolicies = new ArrayList<PolicyTemplate>();
		String logMsg = "";
		//Map<String, List<String>> mapList = clientPmp.listMechanisms();
		if(clientPmp == null)
			return deployedPolicies;
		Set<XmlPolicy> policies = null;
		try {
		 policies = clientPmp.listPoliciesPmp();
		}
		catch (Exception ex){
			_logger.error("clientPMP list policies error");
			return deployedPolicies;
		}
		logMsg += "PMP list policies: "+ policies;
		_logger.info(logMsg);
		for (Iterator<XmlPolicy> iterator = policies.iterator(); iterator.hasNext();) {
			XmlPolicy xmlPolicy = (XmlPolicy) iterator.next();
			String dClass = xmlPolicy.getDataClass();
			if(!dClass.equals(dataClass))
				continue;
			String id = xmlPolicy.getTemplateId();
			String name = xmlPolicy.getName();
			String description= xmlPolicy.getDescription();
			String template = xmlPolicy.getTemplateXml();
			PolicyTemplate pTemplate = new PolicyTemplate(id, new String[]{dClass}, description, template);
			pTemplate.setName(name);
			
			logMsg = "Deployed Policy: "+name +" "+ xmlPolicy.getXml();
			_logger.info(logMsg);
			deployedPolicies.add(pTemplate);
		}
		
		return deployedPolicies;
	}

	public void revokePolicies(Set<IContainer> representations,	String policyClass, List<PolicyTemplate> policies) {
		String log = "";
		for(PolicyTemplate p : policies){
			String policyName = p.getName();
			if(p.equals(""))
				continue;
			
			IStatus status = clientPmp.revokePolicyPmp(policyName);
			log = "Revoked policy: " + status.getEStatus() + " " + policyName;
			_logger.info(log);
		}
		
	}
	
}

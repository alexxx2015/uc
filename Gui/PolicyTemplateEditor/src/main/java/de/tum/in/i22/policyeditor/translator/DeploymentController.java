package de.tum.in.i22.policyeditor.translator;

import java.util.List;
import java.util.Set;

import de.tum.in.i22.policyeditor.logger.EditorLogger;
import de.tum.in.i22.policyeditor.model.PolicyTemplate;
import de.tum.in.i22.policyeditor.model.UserClass;
import de.tum.in.i22.policyeditor.model.UserObject;
import de.tum.in.i22.uc.thrift.types.TContainer;

public class DeploymentController {

	private static EditorLogger logger = EditorLogger.instance();
	
	
	/**
	 * For all the data representations of the data,
	 * deploy all the policies.
	 * @param dataRepresentations
	 * @param dataClass
	 * @param policies
	 */
	public void deployPolicies(Set<TContainer> dataRepresentations, String dataClass, List<PolicyTemplate> policies){
		String message = "policies translated: "+ policies.size();
		logger.infoLog(message, null);
		
		UserObject obj = new UserObject("dummyObject");
		
		UserClass dataClassObj = new UserClass(dataClass);
		for (PolicyTemplate policy : policies) {
			
			policy.instantiatePolicyClass();
			policy.instantiatePolicyObject(obj);
			
			logger.infoLog(policy.toStringExtended(), null);
		}
		
	}
	
}

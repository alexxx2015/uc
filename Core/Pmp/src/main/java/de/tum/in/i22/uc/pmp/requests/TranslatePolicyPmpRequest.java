package de.tum.in.i22.uc.pmp.requests;

import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class TranslatePolicyPmpRequest extends PmpRequest<IPtpResponse> {

	private String requestId;
	private Map<String,String> param;
	private XmlPolicy policy;
	
	public TranslatePolicyPmpRequest(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy){
		this.requestId = requestId;
		this.param = parameters;
		this.policy = xmlPolicy;
	}
	
	@Override
	public IPtpResponse process(PmpProcessor processor) {
		IPtpResponse result = processor.translatePolicy(requestId, param, policy);
		return result;
	}

}

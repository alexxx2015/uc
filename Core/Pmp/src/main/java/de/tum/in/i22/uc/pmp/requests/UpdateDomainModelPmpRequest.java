package de.tum.in.i22.uc.pmp.requests;

import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class UpdateDomainModelPmpRequest extends PmpRequest<IPtpResponse> {

	private String requestId;
	private Map<String,String> param;
	private XmlPolicy domainModel;
	
	public UpdateDomainModelPmpRequest(String requestId, Map<String, String> parameters, XmlPolicy domainModel){
		this.requestId = requestId;
		this.param = parameters;
		this.domainModel = domainModel;
	}
	
	@Override
	public IPtpResponse process(PmpProcessor processor) {
		IPtpResponse result = processor.updateDomainModel(requestId, param, domainModel);
		return result;
	}
	
}

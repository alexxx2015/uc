package de.tum.in.i22.uc.cm.datatypes.basic;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class PtpResponseBasic implements IPtpResponse{

	private IStatus status;
	private XmlPolicy policy;
	private String message;
	
	public PtpResponseBasic(IStatus status, XmlPolicy policy, String message){
		this.status = status;
		this.policy = policy;
		if(message == null)
			message = "";
		this.message = message;
	}
	
	@Override
	public IStatus getStatus() {
		return this.status;
	}

	public void setStatus(IStatus status){
		this.status = status;
	}
	
	public void setPolicy(XmlPolicy policy){
		this.policy = policy;
	}
	
	@Override
	public XmlPolicy getPolicy() {
		return this.policy;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String msg){
		this.message = msg;
	}

}

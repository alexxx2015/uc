package de.tum.in.i22.uc.cm.datatypes.basic;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class PtpResponseBasic implements IPtpResponse{

	private IStatus status;
	private XmlPolicy policy;
	
	public PtpResponseBasic(IStatus status, XmlPolicy policy){
		this.status = status;
		this.policy = policy;
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

}

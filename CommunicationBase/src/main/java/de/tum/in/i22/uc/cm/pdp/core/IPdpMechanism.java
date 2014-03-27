package de.tum.in.i22.uc.cm.pdp.core;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IResponse;

public interface IPdpMechanism {

	IPdpAuthorizationAction getAuthorizationAction();
	
  public long getTimestepSize();
  public void setTimestepSize(long timestepSize);

	void setAuthorizationAction(IPdpAuthorizationAction mAuthorizationAction);

	List<IPdpExecuteAction> getExecuteActions();

	public List<IPdpExecuteAction> getExecuteAsyncActions();
	void setExecuteActions(ArrayList<IPdpExecuteAction> mExecuteActions);

	void addExecuteAction(IPdpExecuteAction mExecuteActionTmp);

	IResponse getResponse();
	
	public String getMechanismName();
	public boolean revoke();

}
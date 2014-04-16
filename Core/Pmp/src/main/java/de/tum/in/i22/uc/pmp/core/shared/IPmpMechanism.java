package de.tum.in.i22.uc.pmp.core.shared;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;

public interface IPmpMechanism {

	IPmpAuthorizationAction getAuthorizationAction();

	public long getTimestepSize();

	public void setTimestepSize(long timestepSize);

	void setAuthorizationAction(IPmpAuthorizationAction mAuthorizationAction);

	List<IPmpExecuteAction> getExecuteActions();

	public List<IPmpExecuteAction> getExecuteAsyncActions();

	void setExecuteActions(ArrayList<IPmpExecuteAction> mExecuteActions);

	void addExecuteAction(IPmpExecuteAction mExecuteActionTmp);

	IResponse getResponse();

	public String getMechanismName();

	public boolean revoke();

}
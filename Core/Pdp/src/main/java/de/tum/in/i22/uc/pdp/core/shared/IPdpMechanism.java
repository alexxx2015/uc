package de.tum.in.i22.uc.pdp.core.shared;

import java.util.List;

import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public interface IPdpMechanism extends Runnable {

	public IPdpAuthorizationAction getAuthorizationAction();

	public long getTimestepSize();

	public List<IPdpExecuteAction> getExecuteActions();

	public List<IPdpExecuteAction> getExecuteAsyncActions();

	public String getName();

	public void revoke();

	public PolicyDecisionPoint getPolicyDecisionPoint();

}
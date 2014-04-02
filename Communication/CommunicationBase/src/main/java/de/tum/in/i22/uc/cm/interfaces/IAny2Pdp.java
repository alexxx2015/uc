package de.tum.in.i22.uc.cm.interfaces;

import java.util.ArrayList;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods to be invoked on a PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pdp {

	/*
	 * From PEP
	 */
	public IResponse notifyEventAsync(IEvent event);
	public IResponse notifyEventSync(IEvent event);

	/*
	 * From PMP
	 */
	public IStatus deployMechanism(IMechanism mechanism);
	public IMechanism exportMechanism(String par);
	public IStatus revokeMechanism(String policyName);
	public IStatus revokeMechanism(String policyName, String mechName);
	public IStatus deployPolicy(String policyFilePath);
	public Map<String, ArrayList<IMechanism>> listMechanisms();

	/*
	 * From PXP
	 */
	public boolean registerPxp(IPxpSpec pxp);
}

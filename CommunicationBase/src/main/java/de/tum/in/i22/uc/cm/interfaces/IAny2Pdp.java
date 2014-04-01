package de.tum.in.i22.uc.cm.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.pdp.core.IPdpMechanism;



/**
 * Incoming interface. Other modules use this interface to invoke operations
 * on PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pdp extends IAny2Generic<IAny2Pip,IAny2Pmp>  {

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
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();

	/*
	 * From PXP
	 */
	public boolean registerPxp(IPxpSpec pxp);
}

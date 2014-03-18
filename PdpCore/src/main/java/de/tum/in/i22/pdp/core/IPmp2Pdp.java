package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.IConnection;

public interface IPmp2Pdp extends IConnection {
	public IStatus deployMechanism(IMechanism mechanism);
	public IMechanism exportMechanism(String par);
	public IStatus revokeMechanism(String policyName);
	public IStatus revokeMechanism(String policyName, String mechName);
	public IStatus deployPolicy(String policyFilePath);
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
}

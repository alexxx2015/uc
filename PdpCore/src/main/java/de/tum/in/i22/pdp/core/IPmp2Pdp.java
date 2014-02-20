package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPmp2Pdp {
	public IStatus deployMechanism(IMechanism mechanism);
	public IMechanism exportMechanism(String par);
	public IStatus revokeMechanism(String policyName);
	public IStatus revokeMechanism(String policyName, String mechName);
	public IStatus deployPolicy(String policyFilePath);
	public HashMap<String, ArrayList<Mechanism>> listMechanisms();
}

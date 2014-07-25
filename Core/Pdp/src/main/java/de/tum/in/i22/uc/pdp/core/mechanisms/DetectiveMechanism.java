package de.tum.in.i22.uc.pdp.core.mechanisms;

import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;

class DetectiveMechanism extends Mechanism {

	protected DetectiveMechanism(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp)
			throws InvalidMechanismException {
		super(mech, policyName, pdp);
	}
}

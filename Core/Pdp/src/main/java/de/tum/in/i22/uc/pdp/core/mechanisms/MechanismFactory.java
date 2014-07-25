package de.tum.in.i22.uc.pdp.core.mechanisms;

import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.xsd.DetectiveMechanismType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;

public class MechanismFactory {
	public static Mechanism create(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp) throws InvalidMechanismException {
		if (mech instanceof PreventiveMechanismType) {
			return new PreventiveMechanism(mech, policyName, pdp);
		}
		else if (mech instanceof DetectiveMechanismType) {
			return new DetectiveMechanism(mech, policyName, pdp);
		}

		throw new InvalidMechanismException("" + mech);
	}
}

package de.tum.in.i22.uc.pdp.core;

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
		else {
			throw new InvalidMechanismException(mech.toString());
		}
	}


//	public static Collection<IMechanism> create(PolicyType policy) {
//		Collection<IMechanism> mechanisms;
//
//	}
}

package de.tum.in.i22.uc.pdp.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidPolicyException;
import de.tum.in.i22.uc.pdp.xsd.DetectiveMechanismType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;


/**
 * 
 * @author Florian Kelbert
 *
 */
class MechanismFactory {
	private static Logger _logger = LoggerFactory.getLogger(MechanismFactory.class);

	private final PolicyDecisionPoint _pdp;

	MechanismFactory(PolicyDecisionPoint pdp) {
		_pdp = pdp;
	}

	private Mechanism create(MechanismBaseType mech, String policyName) throws InvalidMechanismException {
		if (mech instanceof PreventiveMechanismType) {
			return new PreventiveMechanism(mech, policyName, _pdp);
		}
		else if (mech instanceof DetectiveMechanismType) {
			return new DetectiveMechanism(mech, policyName, _pdp);
		}
		else {
			throw new InvalidMechanismException(mech.toString());
		}
	}


	Collection<Mechanism> createMechanisms(PolicyType policy) throws InvalidPolicyException {
		Collection<Mechanism> mechanisms = new LinkedList<>();

		List<MechanismBaseType> mechs = policy.getDetectiveMechanismOrPreventiveMechanism();

		for (MechanismBaseType m : mechs) {
			try {
				mechanisms.add(create(m, policy.getName()));
			} catch (InvalidMechanismException e) {
				_logger.error("Invalid mechanism specified: {} ({})", m.getName(), e.getMessage());
			}
		}

		if (mechanisms.size() != mechs.size()) {
			_logger.error("Policy {} is invalid, because at least one of its mechanisms is invalid.", policy.getName());
			throw new InvalidPolicyException("Policy " + policy.getName() + " is invalid, because at least one of its mechanisms is invalid.");
		}

		return mechanisms;
	}
}

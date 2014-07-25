package de.tum.in.i22.uc.pdp.core.mechanisms;

import java.util.HashMap;

import de.tum.in.i22.uc.pdp.core.ActionDescriptionStore;
import de.tum.in.i22.uc.pdp.core.AuthorizationAction;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationActionType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;

class PreventiveMechanism extends Mechanism {

	PreventiveMechanism(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp)
			throws InvalidMechanismException {
		super(mech, policyName, pdp);

		PreventiveMechanismType curMech = (PreventiveMechanismType) mech;
		_logger.debug("Processing PreventiveMechanism");

		ActionDescriptionStore ads = pdp.getActionDescriptionStore();
		ads.addMechanism(this);

		// TODO: subscription to PEP?!

		_logger.debug("Preparing AuthorizationAction from List<AuthorizationActionType>: {} entries", curMech
				.getAuthorizationAction().size());
		HashMap<String, AuthorizationAction> authActions = new HashMap<String, AuthorizationAction>();

		for (AuthorizationActionType auth : curMech.getAuthorizationAction()) {
			_logger.debug("Found authAction {}", auth.getName());
			if (auth.isSetStart() || curMech.getAuthorizationAction().size() == 1)
				authActions.put("start", new AuthorizationAction(auth));
			else
				authActions.put(auth.getName(), new AuthorizationAction(auth));
		}

		_logger.debug("Preparing hierarchy of authorizationActions (list: {})", authActions.size());
		_authorizationAction = authActions.get("start");

		if (curMech.getAuthorizationAction().size() > 1) {
			AuthorizationAction curAuth = _authorizationAction;
			_logger.debug("starting with curAuth: {}", curAuth.getName());
			do {
				_logger.debug("searching for fallback={}", curAuth.getFallbackName());
				if (!curAuth.getFallbackName().equalsIgnoreCase("allow")
						&& !curAuth.getFallbackName().equalsIgnoreCase("inhibit")) {
					AuthorizationAction fallbackAuth = authActions.get(curAuth.getFallbackName());
					if (fallbackAuth == null) {
						_logger.error("Requested fallback authorizationAction {} not found!", curAuth.getFallbackName());
						throw new InvalidMechanismException("Requested fallback authorizationAction not specified");
					}
					curAuth.setFallback(fallbackAuth);
					_logger.debug("  set fallback to {}", curAuth.getFallback().getName());
				} else {
					if (curAuth.getFallbackName().equalsIgnoreCase("allow")) {
						curAuth.setFallback(AuthorizationAction.AUTHORIZATION_ALLOW);
					}
					_logger.debug("  set fallback to static {}", curAuth.getFallback().getName());
					break;
				}
				curAuth = curAuth.getFallback();
			} while (true);
		}
		_logger.debug("AuthorizationActions successfully processed.");
	}

}

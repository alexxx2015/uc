package de.tum.in.i22.uc.pdp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationActionType;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationAllowType;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationInhibitType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteActionType;
import de.tum.in.i22.uc.pdp.xsd.ParameterType;

public class AuthorizationAction implements Serializable {
	private static final long serialVersionUID = 3456284152512343695L;
	private static Logger _logger = LoggerFactory.getLogger(AuthorizationAction.class);

	public static final AuthorizationAction AUTHORIZATION_INHIBIT = new AuthorizationAction("INHIBIT", Authorization.INHIBIT);
	public static final AuthorizationAction AUTHORIZATION_ALLOW = new AuthorizationAction("ALLOW", Authorization.ALLOW);

	private Authorization _auth = Authorization.INHIBIT;
	private String _name = "";
	private AuthorizationAction _fallback = AUTHORIZATION_INHIBIT;
	private String _fallbackName = "";
	private final List<ExecuteAction> _executeSyncActions = new ArrayList<>();

	/*
	 * Parameter modifiers
	 */
	private final Map<String,String> _modifiers = new HashMap<>();

	private long _delay = 0;

	public AuthorizationAction() {
	}

	AuthorizationAction(String name, Authorization auth) {
		_name = name;
		_auth = auth;
	}

	public AuthorizationAction(AuthorizationActionType auth) {
		_logger.debug("Preparing AuthorizationAction from AuthorizationActionType");
		_name = auth.getName();
		_fallbackName = auth.getFallback();

		Object obj = auth.getAllowOrInhibit();
		if (obj instanceof AuthorizationAllowType) {
			AuthorizationAllowType allow = (AuthorizationAllowType) obj;

			_auth = Authorization.ALLOW;
			try {
				if (allow.getDelay() != null) {
					_delay = allow.getDelay().getAmount() * TimeAmount.getTimeUnitMultiplier(allow.getDelay().getUnit());
				}

				if (allow.getModify() != null) {
					for (ParameterType param : allow.getModify().getParameter()) {
						_logger.debug("modify: {} -> {}", param.getName(), param.getValue());
						_modifiers.put(param.getName(), param.getValue());
					}
				}
				for (ExecuteActionType execAction : allow.getExecuteSyncAction()) {
					_executeSyncActions.add(new ExecuteAction(execAction));
				}
			} catch (Exception e) {
				_logger.error("exception occured...");
				e.printStackTrace();
				_logger.error(e.getMessage());
			}
		}
		else {
			AuthorizationInhibitType inhibit = (AuthorizationInhibitType) obj;

			_auth = Authorization.INHIBIT;
			if (inhibit.getDelay() != null) {
				_delay = inhibit.getDelay().getAmount() * TimeAmount.getTimeUnitMultiplier(inhibit.getDelay().getUnit());
			}
		}
	}

	Authorization getAuthorization() {
		return _auth;
	}

	List<ExecuteAction> getExecuteActions() {
		return _executeSyncActions;
	}

	Map<String,String> getModifiers() {
		return Collections.unmodifiableMap(_modifiers);
	}

	void addModifier(String name, String value) {
		_modifiers.put(name, value);
	}

	long getDelay() {
		return _delay;
	}

	void setDelay(long delay) {
		_delay = delay;
	}

	void setAuthorization(Authorization auth) {
		_auth = auth;
	}

	public String getName() {
		return _name;
	}

	public AuthorizationAction getFallback() {
		return _fallback == null ? AUTHORIZATION_INHIBIT : _fallback;
	}

	public void setFallback(AuthorizationAction fallback) {
		_fallback = fallback;
	}

	public String getFallbackName() {
		return _fallbackName;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("_name", _name)
				.add("_type", _auth)
				.add("_delay", _delay)
				.add("_modifiers", _modifiers)
				.add("_executeAction", _executeSyncActions)
				.add("_fallback", _fallback)
				.add("_fallbackName", _fallbackName)
				.toString();
	}

	public enum Authorization {
		ALLOW,
		INHIBIT;
	}
}

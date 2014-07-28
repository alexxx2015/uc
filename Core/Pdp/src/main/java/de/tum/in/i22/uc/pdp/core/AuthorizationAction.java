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
	private List<ExecuteAction> _executeSyncActions = new ArrayList<ExecuteAction>();

	/*
	 * Parameter modifiers
	 */
	private final Map<String,String> _modifiers = new HashMap<>();

	private long _delay = 0;

	public AuthorizationAction() {
	}

	public AuthorizationAction(int start, String name, Authorization auth, List<ExecuteAction> executeActions,
			long delay, Map<String,String> modifiers, AuthorizationAction fallback) {
		_auth = auth;
		if (name != null)
			_name = name;
		if (executeActions != null)
			_executeSyncActions = executeActions;
		if (fallback != null)
			_fallback = fallback;
		if (auth == Authorization.ALLOW) {
			_modifiers.putAll(modifiers);;
			_delay = delay;
		}
	}

	public AuthorizationAction(String name, Authorization auth) {
		_name = name;
		_auth = auth;
	}

	public AuthorizationAction(AuthorizationActionType auth) {
		_logger.debug("Preparing AuthorizationAction from AuthorizationActionType");
		_name = auth.getName();
		_fallbackName = auth.getFallback();

		Object obj = auth.getAllowOrInhibit();
		AuthorizationAllowType allow = null;
		AuthorizationInhibitType inhibit = null;

		if (obj instanceof AuthorizationAllowType)
			allow = (AuthorizationAllowType) obj;
		else
			inhibit = (AuthorizationInhibitType) obj;

		if (inhibit != null) {
			_auth = Authorization.INHIBIT;
			if (inhibit.getDelay() != null)
				_delay = inhibit.getDelay().getAmount()
						* TimeAmount.getTimeUnitMultiplier(inhibit.getDelay().getUnit());
		} else {
			_auth = Authorization.ALLOW;
			try {
				if (allow.getDelay() != null)
					_delay = allow.getDelay().getAmount()
							* TimeAmount.getTimeUnitMultiplier(allow.getDelay().getUnit());
				else
					_delay = 0;

				if (allow.getModify() != null) {
					for (ParameterType param : allow.getModify().getParameter()) {
						_logger.debug("modify: {} -> {}", param.getName(), param.getValue());
						// TODO: use different parameter types?!
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
	}

	public Authorization getAuthorization() {
		return _auth;
	}

	public List<ExecuteAction> getExecuteActions() {
		return _executeSyncActions;
	}

	public Map<String,String> getModifiers() {
		return Collections.unmodifiableMap(_modifiers);
	}

	public void addModifier(String name, String value) {
		_modifiers.put(name, value);
	}

	public long getDelay() {
		return _delay;
	}

	public void setDelay(long delay) {
		_delay = delay;
	}

	public void setAuthorization(Authorization auth) {
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

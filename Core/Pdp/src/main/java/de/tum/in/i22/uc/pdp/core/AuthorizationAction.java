package de.tum.in.i22.uc.pdp.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ParamBasic;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.shared.Constants;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationActionType;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationAllowType;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationInhibitType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteActionType;
import de.tum.in.i22.uc.pdp.xsd.ParameterType;

public class AuthorizationAction implements Serializable {
	private static final long serialVersionUID = 3456284152512343695L;
	private static Logger _logger = LoggerFactory.getLogger(AuthorizationAction.class);

	public static final AuthorizationAction AUTHORIZATION_INHIBIT = new AuthorizationAction("INHIBIT", Constants.AUTHORIZATION_INHIBIT);
	public static final AuthorizationAction AUTHORIZATION_ALLOW = new AuthorizationAction("ALLOW", Constants.AUTHORIZATION_ALLOW);

	private boolean _type = false;
	private String _name = "";
	private AuthorizationAction _fallback = AUTHORIZATION_INHIBIT;
	private String _fallbackName = "";
	private List<ExecuteAction> _executeSyncActions = new ArrayList<ExecuteAction>();
	private List<ParamBasic> _modifiers = new ArrayList<>();
	private long _delay = 0;

	public AuthorizationAction() {
	}

	public AuthorizationAction(int start, String name, boolean type, List<ExecuteAction> executeActions,
			long delay, List<ParamBasic> modifiers, AuthorizationAction fallback) {
		_type = type;
		if (name != null)
			_name = name;
		if (executeActions != null)
			_executeSyncActions = executeActions;
		if (fallback != null)
			_fallback = fallback;
		if (type == Constants.AUTHORIZATION_ALLOW) {
			_modifiers = modifiers;
			_delay = delay;
		}
	}

	public AuthorizationAction(String name, boolean type) {
		_name = name;
		_type = type;
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
			_type = Constants.AUTHORIZATION_INHIBIT;
			if (inhibit.getDelay() != null)
				_delay = inhibit.getDelay().getAmount()
						* TimeAmount.getTimeUnitMultiplier(inhibit.getDelay().getUnit());
		} else {
			_type = Constants.AUTHORIZATION_ALLOW;
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
						_modifiers.add(new ParamBasic(param.getName(), param.getValue()));
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

	public boolean getAuthorizationAction() {
		return _type;
	}

	public List<ExecuteAction> getExecuteActions() {
		return _executeSyncActions;
	}

	public List<ParamBasic> getModifiers() {
		return _modifiers;
	}

	public void setExecuteActions(List<ExecuteAction> executeActions) {
		_executeSyncActions = executeActions;
	}

	public void setModifiers(List<ParamBasic> modifiers) {
		if (_type == Constants.AUTHORIZATION_ALLOW)
			_modifiers = modifiers;
	}

	public void addModifier(ParamBasic parameter) {
		_modifiers.add(parameter);
	}

	public void addExecuteAction(ExecuteAction executeAction) {
		_executeSyncActions.add(executeAction);
	}

	public long getDelay() {
		return _delay;
	}

	public void setDelay(long delay) {
		_delay = delay;
	}

	public boolean getType() {
		return _type;
	}

	public void setType(boolean type) {
		_type = type;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		if (name != null)
			_name = name;
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
				.add("_type", _type)
				.add("_delay", _delay)
				.add("_modifiers", _modifiers)
				.add("_executeAction", _executeSyncActions)
				.add("_fallback", _fallback)
				.add("_fallbackName", _fallbackName)
				.toString();
	}
}

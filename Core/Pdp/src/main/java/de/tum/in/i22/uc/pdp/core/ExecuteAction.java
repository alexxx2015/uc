package de.tum.in.i22.uc.pdp.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ParamBasic;
import de.tum.in.i22.uc.pdp.xsd.ExecuteActionType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.ParameterType;

public class ExecuteAction implements Serializable {
	private static final long serialVersionUID = 8451999937686098519L;
	private static Logger _logger = LoggerFactory.getLogger(ExecuteAction.class);

	private String _name = null;
	private final Map<String,ParamBasic> _parameters = new HashMap<>();
	private String _processor = null;
	private String _id = null;

	private ExecuteAction(String name, String id, Collection<ParameterType> params) {
		_name = name;
		_id = id;
		for (ParameterType param : params) {
			_parameters.put(param.getName(), new ParamBasic(param.getName(), param.getValue()));
		}
	}

	public ExecuteAction(ExecuteActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter());
		_logger.debug("Preparing executeAction from ExecuteActionType");
	}

	public ExecuteAction(ExecuteAsyncActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter());
		_logger.debug("Preparing executeAction from ExecuteAsyncActionType");
		_processor = execAction.getProcessor().value();
	}

	public String getName() {
		return _name;
	}

	public Collection<ParamBasic> getParameters() {
		return _parameters.values();
	}

	public ParamBasic getParameterForName(String name) {
		return _parameters.get(name);
	}

	public String getProcessor() {
		return _processor;
	}

	public String getId() {
		return _id;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this).add("name", _name).add("id", _id)
				.add(_processor, _processor).add("parameters", _parameters).toString();
	}
}

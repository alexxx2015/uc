package de.tum.in.i22.uc.pdp.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.xsd.ExecuteActionType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.ParameterType;

public class ExecuteAction implements Serializable {
	private static final long serialVersionUID = 8451999937686098519L;
	private static Logger _logger = LoggerFactory.getLogger(ExecuteAction.class);

	private final String _name;
	private final Map<String,String> _parameters = new HashMap<>();
	private final String _processor;
	private final String _id;

	private ExecuteAction(String name, String id, Collection<ParameterType> params, String processor) {
		_name = name;
		_id = id;
		_processor = processor;

		for (ParameterType param : params) {
			_parameters.put(param.getName(), param.getValue());
		}
	}

	public ExecuteAction(ExecuteActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter(), null);
		_logger.debug("Preparing executeAction from ExecuteActionType");
	}

	public ExecuteAction(ExecuteAsyncActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter(), execAction.getProcessor().value());
		_logger.debug("Preparing executeAction from ExecuteAsyncActionType");
	}

	public String getName() {
		return _name;
	}

	public Map<String,String> getParameters() {
		return Collections.unmodifiableMap(_parameters);
	}

	public String getProcessor() {
		return _processor;
	}

	public String getId() {
		return _id;
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(this)
				.add("_name", _name)
				.add("_id", _id)
				.add("_processor", _processor)
				.add("_parameters", _parameters)
				.toString();
	}
}

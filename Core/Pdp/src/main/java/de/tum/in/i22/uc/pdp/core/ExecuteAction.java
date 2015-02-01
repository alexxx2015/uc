package de.tum.in.i22.uc.pdp.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.pdp.xsd.ExecuteActionType;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.ParameterType;

public class ExecuteAction extends EventBasic {
	private static final long serialVersionUID = -1885601336140178748L;

	private static Logger _logger = LoggerFactory.getLogger(ExecuteAction.class);

	private final String _processor;
	private final String _pxpId;

	private ExecuteAction(String name, String pxpId, Collection<ParameterType> params, String processor) {
		super(name, convert(params));

		_pxpId = pxpId;
		_processor = processor;
	}

	public ExecuteAction(ExecuteActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter(), null);
		_logger.debug("Preparing executeAction from ExecuteActionType");
	}

	public ExecuteAction(ExecuteAsyncActionType execAction) {
		this(execAction.getName(), execAction.getId(), execAction.getParameter(), execAction.getProcessor().value());
		_logger.debug("Preparing executeAction from ExecuteAsyncActionType");
	}

	public String getProcessor() {
		return _processor;
	}

	public String getPxpId() {
		return _pxpId;
	}

	private static Map<String,String> convert(Collection<ParameterType> paramsIn) {
		if (paramsIn == null) {
			return Collections.emptyMap();
		}

		Map<String,String> paramsOut = new HashMap<>();
		paramsIn.forEach(p -> paramsOut.put(p.getName(), p.getValue()));
		return paramsOut;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_name", super.getName())
				.add("_pxpId", _pxpId)
				.add("_processor", _processor)
				.add("_parameters", super.getParameters())
				.toString();
	}
}

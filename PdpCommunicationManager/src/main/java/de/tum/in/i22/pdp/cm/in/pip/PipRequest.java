package de.tum.in.i22.pdp.cm.in.pip;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.methods.EPipRequestMethod;
import de.tum.in.i22.uc.cm.methods.EPipResponse;

/**
 * Class for handling remote PIP requests.
 * @author Florian Kelbert
 *
 */
public class PipRequest {
	private final EPipRequestMethod _method;
	private Collection<IData> _data = null;
	private Collection<IContainer> _containers = null;
	private IEvent _event = null;
	private EPipResponse _response = null;

	public PipRequest(EPipRequestMethod method, IEvent event, Collection<IData> data, Collection<IContainer> containers, EPipResponse response) {
		if (response == null
			|| (method == EPipRequestMethod.HAS_ALL_DATA && !paramsPresent(data))
			|| (method == EPipRequestMethod.HAS_ANY_DATA && !paramsPresent(data))
			|| (method == EPipRequestMethod.HAS_ALL_CONTAINERS && !paramsPresent(containers))
			|| (method == EPipRequestMethod.HAS_ANY_CONTAINER && !paramsPresent(containers))
			|| (method == EPipRequestMethod.NOTIFY_ACTUAL_EVENT && !paramsPresent(event))
			|| (method == EPipRequestMethod.NOTIFY_DATA_TRANSFER && !paramsPresent(data, containers))) {
				throw new RuntimeException("Method parameter combination in PipRequest not possible!");
		}

		_method = method;
		_data = data;
		_containers = containers;
		_event = event;
		_response = response;
	}

	private boolean paramsPresent(Object ... params) {
		boolean result = true;

		for (int i = 0; i < params.length && result; i++) {
			result = result && params[i] != null;
			if (params[i] instanceof Collection<?>) {
				result = result && ((Collection<?>) params[i]).size() > 0;
			}
		}

		return result;
	}

	public PipRequest(IEvent event, EPipResponse response) {
		this(EPipRequestMethod.NOTIFY_ACTUAL_EVENT, event, null, null, response);
	}

//	public PipRequest(EPipRequestMethod method, IData ... data) {
//		this(method, null, new HashSet<IData>(Arrays.asList(data)), null, null);
//	}
//
//	public PipRequest(EPipRequestMethod method, IContainer ... container) {
//		this(method, null, null, new HashSet<IContainer>(Arrays.asList(container)), null);
//	}
//
//	public PipRequest(EPipRequestMethod method, IContainer ... containers, IData ... data) {
//		this(method, null, new HashSet<IData>(Arrays.asList(data)),
//				new HashSet<IContainer>(Arrays.asList(containers)), null);
//	}

	public EPipRequestMethod getMethod() {
		return _method;
	}

	public Collection<IData> getData() {
		return _data;
	}

	public Collection<IContainer> getContainer() {
		return _containers;
	}

	public IEvent getEvent() {
		return _event;
	}

	public EPipResponse getResponse() {
		return _response;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_method", _method)
				.add("_data", _data)
				.add("_container", _containers)
				.add("_event", _event)
				.toString();
	}
}

package de.tum.in.i22.uc.cm.requests;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

/**
 * Class for handling remote PIP requests.
 * @author Florian Kelbert
 *
 */
public class PipRequest<R> extends Request<R> {
	private final EPipRequestType _type;
	private Collection<IData> _data = null;
	private Collection<IContainer> _containers = null;
	private IEvent _event = null;

	private PipRequest(EPipRequestType type, IEvent event, Collection<IData> data, Collection<IContainer> containers, Class<R> responseClass) {
		super(responseClass);
		if ((type == EPipRequestType.HAS_ALL_DATA && !paramsPresent(data))
			|| (type == EPipRequestType.HAS_ANY_DATA && !paramsPresent(data))
			|| (type == EPipRequestType.HAS_ALL_CONTAINERS && !paramsPresent(containers))
			|| (type == EPipRequestType.HAS_ANY_CONTAINER && !paramsPresent(containers))
			|| (type == EPipRequestType.NOTIFY_ACTUAL_EVENT && !paramsPresent(event))
			|| (type == EPipRequestType.NOTIFY_DATA_TRANSFER && !paramsPresent(data, containers))) {
				throw new RuntimeException("Method parameter combination in PipRequest not possible!");
		}

		_type = type;
		_data = data;
		_containers = containers;
		_event = event;
	}

	public PipRequest(IEvent event, Class<R> responseClass) {
		this(EPipRequestType.NOTIFY_ACTUAL_EVENT, event, null, null, responseClass);
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

	public EPipRequestType getType() {
		return _type;
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

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_method", _type)
				.add("_data", _data)
				.add("_container", _containers)
				.add("_event", _event)
				.toString();
	}

	public enum EPipRequestType {
		EVALUATE_PREDICATE,
		GET_CONTAINER_FOR_DATA,
		GET_DATA_IN_CONTAINER,
		NOTIFY_ACTUAL_EVENT,
		UPDATE_INFORMATION_FLOW_SEMANTICS,
		HAS_ALL_DATA,
		HAS_ANY_DATA,
		HAS_ALL_CONTAINERS,
		HAS_ANY_CONTAINER,
		NOTIFY_DATA_TRANSFER;
	}
}

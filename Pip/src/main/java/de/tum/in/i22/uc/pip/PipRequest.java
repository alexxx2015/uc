package de.tum.in.i22.uc.pip;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.processing.Request;

/**
 * Class for handling remote PIP requests.
 * @author Florian Kelbert
 *
 */
public abstract class PipRequest<R> extends Request<R, PipProcessor> {
	protected final Collection<IData> _data = null;
	protected final Collection<IContainer> _containers = null;
	protected IEvent _event = null;

//	private PipRequest(IEvent event, Collection<IData> data, Collection<IContainer> containers) {
//		if ((type == EPipRequestType.HAS_ALL_DATA && !paramsPresent(data))
//			|| (type == EPipRequestType.HAS_ANY_DATA && !paramsPresent(data))
//			|| (type == EPipRequestType.HAS_ALL_CONTAINERS && !paramsPresent(containers))
//			|| (type == EPipRequestType.HAS_ANY_CONTAINER && !paramsPresent(containers))
//			|| (type == EPipRequestType.NOTIFY_ACTUAL_EVENT && !paramsPresent(event))
//			|| (type == EPipRequestType.NOTIFY_DATA_TRANSFER && !paramsPresent(data, containers))) {
//				throw new RuntimeException("Method parameter combination in PipRequest not possible!");
//		}
//
//		_data = data;
//		_containers = containers;
//		_event = event;
//	}

	public PipRequest(IEvent event) {
		_event = event;
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

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_data", _data)
				.add("_container", _containers)
				.add("_event", _event)
				.toString();
	}
}

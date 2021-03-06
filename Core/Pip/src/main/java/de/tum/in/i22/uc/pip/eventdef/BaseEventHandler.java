package de.tum.in.i22.uc.pip.eventdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;


public abstract class BaseEventHandler implements IEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();
	protected static final Logger _logger = LoggerFactory.getLogger(BaseEventHandler.class);

	protected IEvent _event;

	protected IAnyInformationFlowModel _informationFlowModel;
	protected IDistributionManager _distributionManager;

	protected final IStatus STATUS_OKAY = _messageFactory.createStatus(EStatus.OKAY);
	protected final IStatus STATUS_ERROR = _messageFactory.createStatus(EStatus.ERROR);

	protected BaseEventHandler() {	}

	/*
	 * This function describes how the event updates the information flow model..
	 */
	protected abstract IStatus update();


	/*
	 * In this function, we describe what happens when a certain event is
	 * executed.
	 */
	@Override
	public IStatus performUpdate() {
		if (_event == null) {
			return _messageFactory.createStatus(EStatus.ERROR);
		}

		/*
		 * Update the ifModel according to the single event semantics
		 */
		return update();
	}

	@Override
	public final IEventHandler setEvent(IEvent event) {
		if (_event != null) {
			throw new RuntimeException("Event already set. Can only be set once.");
		}

		_event = event;
		return this;
	}

	@Override
	public final void setInformationFlowModel(IAnyInformationFlowModel ifm) {
		if (_informationFlowModel != null) {
			throw new RuntimeException("Information Flow Model already set. Can only be set once.");
		}

		_informationFlowModel = ifm;
	}


	@Override
	public void setDistributionManager(IDistributionManager distributionManager) {
		if (_distributionManager != null) {
			throw new RuntimeException("DistributionManager already set. Can only be set once.");
		}
		_distributionManager = distributionManager;
	}

	protected final String getParameterValue(String key) throws ParameterNotFoundException {
		String value = _event.getParameters().get(key);

		if (value == null) {
			throw new ParameterNotFoundException(key);
		}
		return value;
	}

	@Override
	public void reset(){
		_event = null;
		_informationFlowModel = null;
		_distributionManager = null;
	}
}

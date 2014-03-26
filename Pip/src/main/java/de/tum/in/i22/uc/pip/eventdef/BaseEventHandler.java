package de.tum.in.i22.uc.pip.eventdef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;


public abstract class BaseEventHandler implements IEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();
	protected static final Logger _logger = LoggerFactory.getLogger(BaseEventHandler.class);

	private IEvent _event;

	protected BasicInformationFlowModel basicIfModel = InformationFlowModelManager.getInstance().getBasicInformationFlowModel();

	protected final IStatus STATUS_OKAY = _messageFactory.createStatus(EStatus.OKAY);
	protected final IStatus STATUS_ERROR = _messageFactory.createStatus(EStatus.ERROR);


	public BaseEventHandler() {	}

	/*
	 * This function describes how the event updates the information flow model..
	 */
	public abstract IStatus execute();

	/*
	 * In this function, we describe what happens when a certain event is
	 * executed.
	 */
	@Override
	public IStatus executeEvent() {

		if (_event == null)
			return _messageFactory.createStatus(EStatus.ERROR);

		IStatus finalStatus = _messageFactory.createStatus(EStatus.OKAY);
		String errorString = "";


		/*
		 * Update the ifModel according to the single event semantics
		 */
		_logger.info(this.getClass().getSimpleName() + " event handler execute");
		execute();


		if (finalStatus.isSameStatus(_messageFactory.createStatus(EStatus.ERROR)))
			finalStatus.setErrorMessage(errorString);

		return finalStatus;

	}

	@Override
	public final IEventHandler setEvent(IEvent event) {
		if (_event != null) {
			throw new RuntimeException("Event already set. Can only be set once.");
		}

		_event = event;
		return this;
	}

	protected final String getParameterValue(String key) throws ParameterNotFoundException {
		String value = _event.getParameters().get(key);

		if (value == null) {
			throw new ParameterNotFoundException(key);
		}
		return value;
	}
}

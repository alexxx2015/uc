package de.tum.in.i22.pip.core.eventdef;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.pip.core.IEventHandler;
import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Scope;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public abstract class BaseEventHandler implements IEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();
	protected static final Logger _logger = LoggerFactory.getLogger(BaseEventHandler.class);

	private IEvent _event;

	protected InformationFlowModel ifModel = InformationFlowModel.getInstance();

	protected final IStatus STATUS_OKAY = _messageFactory.createStatus(EStatus.OKAY);
	protected final IStatus STATUS_ERROR = _messageFactory.createStatus(EStatus.ERROR);

	/*
	 * scopes affected by the current event execution
	 */
	protected static final String _delimiterName = "delimiter";
	protected static final String _openDelimiter = "start";
	protected static final String _closeDelimiter = "end";

	protected static final String _directionName = "direction";
	protected static final String _genericInDirection = "IN";
	protected static final String _genericOutDirection = "OUT";

	protected Set<Scope> _scopesToBeOpened = null;
	protected Set<Scope> _scopesToBeClosed = null;

	public BaseEventHandler() {
		super();
	}

	/*
	 * Event-specific class that creates the scopes (usually only one) that are
	 * opened/closed by the execution of the current event. The scopes to be
	 * opened are stored in _scopesToBeOpened and the scopes to be closed are
	 * stored in _scopesToBeClosed. The function returns the total number of
	 * scopes opened/closed.
	 */

	public int createScope() {
		return 0;
	}

	/*
	 * This function takes as parameter the scope object to be opened, check if
	 * it is already opened and if not, it opens it. It return OKAY if
	 * everything went fine, ERROR if the scope is already opened. It should be
	 * final.
	 */
	protected final IStatus openScope(Scope scope) {
		boolean isOpen = ifModel.isScopeOpened(scope);

		if (isOpen | !(ifModel.openScope(scope))) {
			_logger.info("Scope " + scope + " is already opened");
			return _messageFactory.createStatus(EStatus.ERROR, "Scope" + scope
					+ " is already opened");
		}
		_logger.info("Scope " + scope + " is now open!");

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	/*
	 * This function takes the scope object to be closed as parameter, check if
	 * it is still in the list of active scopes and if that is the case, closes
	 * it. It returns OKAY if everything went fine, ERROR otherwise.
	 *
	 * It should be final.
	 */
	protected final IStatus closeScope(Scope scope) {
		boolean isOpen = ifModel.isScopeOpened(scope);

		if (!(isOpen) | !(ifModel.closeScope(scope))) {
			_logger.info("Scope " + scope + " is already closed");
			return _messageFactory.createStatus(EStatus.ERROR, "Scope" + scope
					+ " is already closed");
		}
		_logger.info("Scope " + scope + " is now closed!");

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	/*
	 * This function describes how the event updates the information flow model,
	 * except for the scope part, which is handled in a generic way by the
	 * functions createScope, openScope and closeScope.
	 */
	public abstract IStatus execute();

	/*
	 * In this function, we describe what happens when a certain event is
	 * executed. Firstly, we update the lists of scopes that are opened/closed
	 * by the execution of the event (_scopesToBeOpened, _scopesToBeclosed).
	 * Secondly, we open all the scopes that needs to be opened. Thirdly, we
	 * update the rest of the IF semantics. Finally, we close all the scopes
	 * that needs to be closed.
	 *
	 * @see de.tum.in.i22.pip.core.IActionHandler#execute_event()
	 */
	@Override
	public IStatus executeEvent() {

		if (_event == null)
			return _messageFactory.createStatus(EStatus.ERROR);

		IStatus finalStatus = _messageFactory.createStatus(EStatus.OKAY);
		String errorString = "";

		/*
		 * 1) create the list of scopes affected by the execution of the current
		 * event and store the number in scopeNum
		 */
		int scopeNum = createScope();

		/*
		 * 2) opens all the scopes to be opened
		 */
		if (scopeNum > 0) {
			if (_scopesToBeOpened != null) {
				for (Scope scope : _scopesToBeOpened) {
					_logger.info("Opening scope "
							+ scope.get_humanReadableName());
					IStatus is = openScope(scope);
					if (!is.isSameStatus(_messageFactory
							.createStatus(EStatus.OKAY))) {
						finalStatus = _messageFactory
								.createStatus(EStatus.ERROR);
						errorString = errorString + "\n" + is.getErrorMessage();
					}
				}
			}
		}

		/*
		 * 3) Update the ifModel according to the single event semantics
		 */

		_logger.info(this.getClass().getSimpleName() + " event handler execute");
		execute();

		/*
		 * 4) Closes all the scopes to be closed
		 */
		if (scopeNum > 0) {
			if (_scopesToBeClosed != null) {
				for (Scope scope : _scopesToBeClosed) {
					_logger.info("Closing scope "
							+ scope.get_humanReadableName());
					IStatus is = closeScope(scope);
					if (!is.isSameStatus(_messageFactory
							.createStatus(EStatus.OKAY))) {
						finalStatus = _messageFactory
								.createStatus(EStatus.ERROR);
						errorString = errorString + "\n" + is.getErrorMessage();
					}
				}
			}
		}

		if (finalStatus.isSameStatus(_messageFactory.createStatus(EStatus.ERROR)))
			finalStatus.setErrorMessage(errorString);

		return finalStatus;

	}

	@Override
	public void setEvent(IEvent event) {
		if (event != null) {
			_event = event;
		}
	}

	protected String getParameterValue(String key) throws ParameterNotFoundException {
		String value = _event.getParameters().get(key);

		if (value == null) {
			throw new ParameterNotFoundException(key);
		}
		return value;
	}

	/**
	 * Checks if the process with given PID already exists, if not create a
	 * container, crate a name, and make a relation between them.
	 *
	 * @param processId
	 *            Process ID (PID)
	 * @return
	 */
	// TODO, FK: This should _NOT_ be part of the *generic* BaseEventHandler
	protected IContainer instantiateProcess(String processId, String processName) {
		IContainer container = ifModel.getContainer(new NameBasic(processId));

		// check if container for process exists and create new container if not
		if (container == null) {
			container = _messageFactory.createContainer();
			ifModel.addName(new NameBasic(processId), container);
			ifModel.addName(new NameBasic(processName), container);
		}

		return container;
	}
}

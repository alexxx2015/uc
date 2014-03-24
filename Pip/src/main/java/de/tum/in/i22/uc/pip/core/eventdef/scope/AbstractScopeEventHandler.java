package de.tum.in.i22.uc.pip.core.eventdef.scope;

import java.util.Set;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.ifm.ScopeInformationFlowModel;
import de.tum.in.i22.uc.pip.core.scope.Scope;


public abstract class AbstractScopeEventHandler extends BaseEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

	private IEvent _event;

	protected ScopeInformationFlowModel ifModel = ScopeInformationFlowModel.getInstance();

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

	public AbstractScopeEventHandler() { }

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
	 * In this function, we describe what happens when a certain event is
	 * executed. Firstly, we update the lists of scopes that are opened/closed
	 * by the execution of the event (_scopesToBeOpened, _scopesToBeclosed).
	 * Secondly, we open all the scopes that needs to be opened. Thirdly, we
	 * update the rest of the IF semantics. Finally, we close all the scopes
	 * that needs to be closed.
	 *
	 * @see de.tum.in.i22.pip.core.IActionHandler#executeEvent()
	 */
	@Override
	public final IStatus executeEvent() {

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
							+ scope.getHumanReadableName());
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
							+ scope.getHumanReadableName());
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
}

package de.tum.in.i22.uc.pip.eventdef.scope;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;


public abstract class AbstractScopeEventHandler extends BaseEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

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

	protected Set<ScopeBasic> _scopesToBeOpened = null;
	protected Set<ScopeBasic> _scopesToBeClosed = null;

	protected AbstractScopeEventHandler() {
		if (_informationFlowModel == null) {
			throw new RuntimeException("Scopes are not supported. Check the configuration.");
		}
	}

	/*
	 * Event-specific class that creates the scopes (usually only one) that are
	 * opened/closed by the execution of the current event. The scopes to be
	 * opened are stored in _scopesToBeOpened and the scopes to be closed are
	 * stored in _scopesToBeClosed. The function returns the total number of
	 * scopes opened/closed.
	 */
	protected int createScope() {
		return 0;
	}

	/*
	 * This function takes as parameter the scope object to be opened, check if
	 * it is already opened and if not, it opens it. It return OKAY if
	 * everything went fine, ERROR if the scope is already opened. It should be
	 * final.
	 */
	protected final IStatus openScope(IScope scope) {
		boolean isOpen = _informationFlowModel.isScopeOpened(scope);

		if (isOpen | !(_informationFlowModel.openScope(scope))) {
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
	protected final IStatus closeScope(IScope scope) {
		boolean isOpen = _informationFlowModel.isScopeOpened(scope);

		if (!(isOpen) | !(_informationFlowModel.closeScope(scope))) {
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
	public
	final IStatus performUpdate() {

		if (_event == null)
			return _messageFactory.createStatus(EStatus.ERROR);

		IStatus finalStatus;

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
				for (IScope scope : _scopesToBeOpened) {
					_logger.info("Opening scope "
							+ scope.getHumanReadableName());
					IStatus is = openScope(scope);
					if (!is.isStatus(EStatus.OKAY)) {
						errorString = errorString + "\n" + is.getErrorMessage();
					}
				}
			}
		}

		/*
		 * 3) Update the ifModel according to the single event semantics
		 */

		_logger.info(this.getClass().getSimpleName() + " event handler execute");
		update();

		/*
		 * 4) Closes all the scopes to be closed
		 */
		if (scopeNum > 0) {
			if (_scopesToBeClosed != null) {
				for (IScope scope : _scopesToBeClosed) {
					_logger.info("Closing scope "
							+ scope.getHumanReadableName());
					IStatus is = closeScope(scope);
					if (!is.isStatus(EStatus.OKAY)) {
						errorString = errorString + "\n" + is.getErrorMessage();
					}
				}
			}
		}

		if (errorString.length() > 0) {
			finalStatus = _messageFactory.createStatus(EStatus.ERROR, errorString);
		}
		else {
			finalStatus = _messageFactory.createStatus(EStatus.OKAY);
		}

		return finalStatus;

	}
}

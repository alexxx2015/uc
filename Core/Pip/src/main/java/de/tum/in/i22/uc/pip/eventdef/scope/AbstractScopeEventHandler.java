package de.tum.in.i22.uc.pip.eventdef.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;

public abstract class AbstractScopeEventHandler extends BaseEventHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator
			.createMessageFactory();

	protected final IStatus STATUS_OKAY = _messageFactory
			.createStatus(EStatus.OKAY);
	protected final IStatus STATUS_ERROR = _messageFactory
			.createStatus(EStatus.ERROR);

	/*
	 * scopes affected by the current event execution
	 */
	protected static final String _delimiterName = Settings.getInstance()
			.getScopeDelimiterName();
	protected static final String _openDelimiter = Settings.getInstance()
			.getScopeOpenDelimiter().toLowerCase();
	protected static final String _closeDelimiter = Settings.getInstance()
			.getScopeCloseDelimiter().toLowerCase();

	protected static final String _directionName = Settings.getInstance()
			.getScopeDirectionName();
	protected static final String _genericInDirection = Settings.getInstance()
			.getScopeGenericInDirection().toLowerCase();
	protected static final String _genericOutDirection = Settings.getInstance()
			.getScopeGenericOutDirection().toLowerCase();

	protected Set<IScope> _scopesToBeOpened = null;
	protected Set<IScope> _scopesToBeClosed = null;

	protected AbstractScopeEventHandler() {
	}

	@Override
	public void reset(){
		super.reset();
		_scopesToBeOpened = null;
		_scopesToBeClosed = null;
		//other parameters don't need to be reset cause they are settings values
	}

	/*
	 * This function describes how the event updates the information flow model
	 * when the event behaves according to cross layer behavior dir.
	 */
	protected IStatus update(EBehavior direction, IScope scope) {
		return new StatusBasic(EStatus.ERROR, direction
				+ " semantics for event " + _event.getName() + " not present");
	}

	/*
	 * Event-specific class that creates the scopes (usually only one) that are
	 * opened/closed by the execution of the current event. The scopes to be
	 * opened are stored in _scopesToBeOpened and the scopes to be closed are
	 * stored in _scopesToBeClosed. The function returns the total number of
	 * scopes opened/closed.
	 */
	private final int createScope() {
		Set<Pair<EScopeState, IScope>> scopeChanges = XDelim(_event);
		if ((scopeChanges == null) || (scopeChanges.size() == 0))
			return 0;
		int res = 0;
		for (Pair<EScopeState, IScope> p : scopeChanges) {
			if (p.getLeft().equals(EScopeState.OPEN)) {
				if (_scopesToBeOpened == null)
					_scopesToBeOpened = new HashSet<IScope>();
				_scopesToBeOpened.add(p.getRight());
				res++;
			}
			if (p.getLeft().equals(EScopeState.CLOSE)) {
				if (_scopesToBeClosed == null)
					_scopesToBeClosed = new HashSet<IScope>();
				_scopesToBeClosed.add(p.getRight());
				res++;
			}
		}
		return res;
	}

	/*
	 * This function takes as parameter the scope object to be opened, check if
	 * it is already opened and if not, it opens it. It return OKAY if
	 * everything went fine, ERROR if the scope is already opened. It should be
	 * final.
	 */
	private final IStatus openScope(IScope scope) {
		IScope os = _informationFlowModel.getOpenedScope(scope);
		boolean isOpen = (os!=null);

		if (isOpen || !(_informationFlowModel.openScope(scope))) {
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
	private final IStatus closeScope(IScope scope) {
		IScope os = _informationFlowModel.getOpenedScope(scope);
		boolean isOpen = (os!=null);

		if (!(isOpen) || !(_informationFlowModel.closeScope(os))) {
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
	public final IStatus performUpdate() {
		if (_event == null)
			return _messageFactory.createStatus(EStatus.ERROR);

		/*
		 * 1) create the list of scopes affected by the execution of the current
		 * event and store the number in scopeNum (XDelim)
		 */
		int scopeNum = createScope();
		_logger.debug("createScope resulted in changes to " + scopeNum
				+ " scopes");

		/*
		 * 2) opens all the scopes to be opened
		 */
		if (_scopesToBeOpened != null) {
			for (IScope scope : _scopesToBeOpened) {
				_logger.info("Opening scope " + scope.getHumanReadableName());
				openScope(scope);
			}
		}

		/*
		 * 3) Update the ifModel according to the single event semantics and the
		 * cross-layer behavior
		 */

		_logger.info(this.getClass().getSimpleName() + " event handler execute");

		Pair<EBehavior, IScope> xlBehavior = XBehav(_event);
		IStatus resStatus = null;

		if (xlBehavior != null) {
			switch ((xlBehavior.getLeft())) {
			case INTRA:
				resStatus = update();
				break;
			case IN:
			case OUT:
			case INTRAIN:
			case INTRAOUT:
				_logger.debug("performUpdate - step 3. cross layer behavior="
						+ xlBehavior.getLeft());
				resStatus = update(xlBehavior.getLeft(),
						xlBehavior.getRight());
				break;
			case UNKNOWN:
				// TODO: implement fallback
				// for the time being perform like a normal INTRA event
				_logger.error("UNKNOWN behavior. Fallback to INTRA behavior.");
				resStatus = update();
			}
		}

		/*
		 * 4) Closes all the scopes to be closed
		 */
		if (_scopesToBeClosed != null) {
			for (IScope scope : _scopesToBeClosed) {
				_logger.info("Closing scope " + scope.getHumanReadableName());
				closeScope(scope);
			}
		}

		return resStatus;

	}

	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		return Pair.of(EBehavior.INTRA, null);
	}

	protected Set<Pair<EScopeState, IScope>> XDelim(IEvent event) {
		return new HashSet<Pair<EScopeState, IScope>>();
	}

	// TODO: XAlias not defined yet
	protected Map<IContainer, Set<IContainer>> XAlias(IEvent event) {
		return new HashMap<IContainer, Set<IContainer>>();
	}

}

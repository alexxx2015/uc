package de.tum.in.i22.uc.pip.extensions.crosslayer;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.ifm.IScopeInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.generic.observable.NotifyingSet;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;

/**
 * Visibility of this class and its methods has been developed carefully. Access
 * via {@link InformationFlowModelManager}.
 *
 * @author Florian Kelbert
 *
 */
public final class ScopeInformationFlowModel extends InformationFlowModelExtension implements IScopeInformationFlowModel {
	private static final Logger _logger = LoggerFactory
			.getLogger(ScopeInformationFlowModel.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_scopeSet", _scopeSet).toString();
	}

	// list of currently opened scopes
	private Set<IScope> _scopeSet;

	// BACKUP TABLES FOR SIMULATION
	private Set<IScope> _scopeSetBackup;

	public ScopeInformationFlowModel(
			InformationFlowModelManager informationFlowModelManager) {
		super(informationFlowModelManager);
		_scopeSet = new NotifyingSet<>(new HashSet<IScope>(), _observer);
		_scopeSetBackup = null;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #reset()
	 */
	@Override
	public void reset() {
		super.reset();
		_scopeSet = new NotifyingSet<>(new HashSet<IScope>(), _observer);
		_scopeSetBackup = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #push()
	 */
	@Override
	public IStatus startSimulation() {
		super.startSimulation();
		_logger.info("Pushing current PIP state...");
		if (_scopeSet != null) {
			_scopeSetBackup = new NotifyingSet<>(new HashSet<IScope>(_scopeSet), _observer);
		}

		return new StatusBasic(EStatus.OKAY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #pop()
	 */
	@Override
	public IStatus stopSimulation() {
		super.stopSimulation();
		_logger.info("Popping current PIP state...");
		_scopeSet = _scopeSetBackup;
		_scopeSetBackup = null;
		return new StatusBasic(EStatus.OKAY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #openScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean openScope(IScope scope) {
		assert (scope != null);
		IScope os = getOpenedScope(scope);
		if (os == null) {
			_ifModel.addName(new NameBasic(scope.getId()),
					new ContainerBasic(), false);
			return _scopeSet.add(scope);
		} else
			return _scopeSet.add(os);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #closeScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean closeScope(IScope scope) {
		assert (scope != null);
		IScope os = getOpenedScope(scope);
		if (os == null)
			return false;
		else {
			_ifModel.remove(_ifModel.getContainer(new NameBasic(os.getId())));
			return _scopeSet.remove(os);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #isScopeOpened(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean isScopeOpened(IScope scope) {
		return _scopeSet.contains(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #getOpenedScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public IScope getOpenedScope(IScope scope) {
		if (scope==null) return null;
		if (isScopeOpened(scope)) {
			for (IScope s : _scopeSet)
				//if a mathc is found return the existing one
				if (scope.equals(s)) return s;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel
	 * #niceString()
	 */
	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";

		sb.append("  Scopes:" + nl);
		if ((_scopeSet == null) || (_scopeSet.size() == 0)) {
			sb.append("Empty" + nl + nl);
			return sb.toString();
		}
		boolean first = true;
		for (IScope scope : _scopeSet) {
			sb.append("    " + scope.getId() + arrow);
			if (first) {
				first = false;
			} else {
				sb.append("    ");
				for (int i = 0; i < scope.getId().length() + arrow.length(); i++) {
					sb.append(" ");
				}
			}
			sb.append(scope.getHumanReadableName());
			sb.append(nl);
		}
		return sb.toString();
	}

	@Override
	public Entry<EBehavior, IScope> XBehav(IEventHandler eventHandler) {
		return null;
	}

	@Override
	public Set<Entry<IScope, EScopeState>> XDelim(IEventHandler eventHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IContainer, Set<IContainer>> XAlias(IEventHandler eventHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	private IEventHandler getEventHandler(IEvent event) {
		IEventHandler eventHandler;
		try {
			eventHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			_logger.error("Could not instantiate event handler for " + event
					+ ", " + e.getMessage());
			return null;
		}

		if (eventHandler == null) {
			_logger.error("Event handler for " + event
					+ " is null. So null is returned");
			return null;
		}

		eventHandler.setEvent(event);
		eventHandler.setInformationFlowModel(_ifModel);

		return eventHandler;
	}

	@Override
	public boolean isSimulating() {
		return _scopeSetBackup != null;
	}
}

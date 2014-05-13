package de.tum.in.i22.uc.pip.extensions.crosslayer;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IScopeInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;

/**
 * Visibility of this class and its methods has been developed carefully.
 * Access via {@link InformationFlowModelManager}.
 *
 * @author Florian Kelbert
 *
 */
public final class ScopeInformationFlowModel extends InformationFlowModelExtension implements IScopeInformationFlowModel {
	private static final Logger _logger = LoggerFactory.getLogger(ScopeInformationFlowModel.class);
	
	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#toString()
	 */
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_scopeSet", _scopeSet)
				.toString();
	}

	// list of currently opened scopes
	private Set<IScope> _scopeSet;

	// BACKUP TABLES FOR SIMULATION
	private Set<IScope> _scopeSetBackup;

	public ScopeInformationFlowModel(InformationFlowModelManager informationFlowModelManager) {
		super(informationFlowModelManager);
		_scopeSet=new HashSet<IScope>();
		_scopeSetBackup=null;
		
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#reset()
	 */
	@Override
	public void reset() {
		_scopeSet = new HashSet<>();
		_scopeSetBackup = null;
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#push()
	 */
	@Override
	public void push() {
		_logger.info("Pushing current PIP state...");
		if (_scopeSet != null) {
			_scopeSetBackup = new HashSet<IScope>(_scopeSet);
		}

	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#pop()
	 */
	@Override
	public void pop() {
		_logger.info("Popping current PIP state...");
		_scopeSet = _scopeSetBackup;
		_scopeSetBackup = null;
	}


	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#openScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean openScope(IScope scope) {
		assert (scope != null);
		_ifModel.addName(new NameBasic(scope.getId()), new ContainerBasic());
		return _scopeSet.add(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#closeScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean closeScope(IScope scope) {
		assert (scope != null);
		_ifModel.remove(_ifModel.getContainer(new NameBasic(scope.getId())));
		return _scopeSet.remove(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#isScopeOpened(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean isScopeOpened(IScope scope) {
		return _scopeSet.contains(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#getOpenedScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public IScope getOpenedScope(IScope scope) {
		// if at least one matching exists...
		if (isScopeOpened(scope)) {

			// ...then clone the set...
			Set<IScope> tmpSet = new HashSet<>(_scopeSet);

			// ...remove it...
			tmpSet.remove(scope);

			// ..and check whether another one exists. If that is the case
			// return null...
			if (tmpSet.contains(scope))
				return null;

			// ..otherwise return the only matching from the original set.
			for (IScope s : _scopeSet)
				if (scope.equals(s))
					return s;
			// Note that we have to iterate until the matching is found again
			// and return that element.
			// This is due to our definition of equality.

			// This line should never be reached. It is added so Eclipse does
			// not complain about the lack of a return value
			assert (false);
			return null;

		} else
			return null;
	}


	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#niceString()
	 */
	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";

		sb.append("  Scopes:" + nl);
		if ((_scopeSet==null)||(_scopeSet.size()==0)){
			sb.append("  Empty" + nl);
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
	
	private IEventHandler getEventHandler(IEvent event){
		IEventHandler eventHandler;
		try {
			eventHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			_logger.error("Could not instantiate event handler for " + event + ", "
							+ e.getMessage());
			return null;
		}

		if (eventHandler == null) {
			_logger.error("Event handler for " + event + " is null. So null is returned");
			return null;
		}

		eventHandler.setEvent(event);
		eventHandler.setInformationFlowModel(_ifModel);
		
		return eventHandler;
	}
	
}

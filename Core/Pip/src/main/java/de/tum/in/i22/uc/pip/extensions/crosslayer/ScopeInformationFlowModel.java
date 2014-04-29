package de.tum.in.i22.uc.pip.extensions.crosslayer;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IScopeInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

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

	public ScopeInformationFlowModel() {
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
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#addScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean addScope(IScope scope) {
		assert (scope != null);
		return _scopeSet.add(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#openScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean openScope(IScope scope) {
		return addScope(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#removeScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean removeScope(IScope scope) {
		assert (scope != null);
		return _scopeSet.remove(scope);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.i22.uc.pip.extensions.crosslayer.IScopeInformationFlowModel#closeScope(de.tum.in.i22.uc.pip.extensions.crosslayer.Scope)
	 */
	@Override
	public boolean closeScope(IScope scope) {
		return removeScope(scope);
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
			Set<IScope> tmpSet = new HashSet<>();
			tmpSet.addAll(_scopeSet);

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

		sb.append("  Scope:" + nl);
		sb.append("    not implemented." + nl);

		sb.append(nl);
		return sb.toString();
	}
}

package de.tum.in.i22.uc.pip.extensions.crosslayer;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

/**
 * Visibility of this class and its methods has been developed carefully.
 * Access via {@link InformationFlowModelManager}.
 *
 * @author Florian Kelbert
 *
 */
class ScopeInformationFlowModel extends InformationFlowModelExtension implements IScopeInformationFlowModel {
	private static final Logger _logger = LoggerFactory.getLogger(ScopeInformationFlowModel.class);

	private static final IInformationFlowModelExtension _instance = new ScopeInformationFlowModel();

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_scopeSet", _scopeSet)
				.toString();
	}

	// list of currently opened scopes
	private Set<Scope> _scopeSet;

	// BACKUP TABLES FOR SIMULATION
	private Set<Scope> _scopeSetBackup;

	private ScopeInformationFlowModel() {
	}

	static IInformationFlowModelExtension getInstance() {
		return _instance;
	}

	@Override
	public void reset() {
		_scopeSet = new HashSet<>();
		_scopeSetBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	@Override
	public void push() {
		_logger.info("Pushing current PIP state...");
		if (_scopeSet != null) {
			_scopeSetBackup = new HashSet<Scope>(_scopeSet);
		}

	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 * @return true if the state has been successfully restored, false otherwise
	 */
	@Override
	public void pop() {
		_logger.info("Popping current PIP state...");
		_scopeSet = _scopeSetBackup;
		_scopeSetBackup = null;
	}

	/**
	 * Adds a new scope to the set.
	 *
	 * @param scope
	 * @return true if the scope is not already present in the set. false
	 *         otherwise.
	 *
	 */
	@Override
	public boolean addScope(Scope scope) {
		assert (scope != null);
		return _scopeSet.add(scope);
	}

	/**
	 * opens a new scope.
	 *
	 * @param the
	 *            new scope to open
	 * @return true if the scope is not already opened. false otherwise.
	 *
	 */
	@Override
	public boolean openScope(Scope scope) {
		return addScope(scope);
	}

	/**
	 * Removes a scope from the set.
	 *
	 * @param scope
	 * @return true if the scope is successfully removed. false otherwise.
	 *
	 */
	@Override
	public boolean removeScope(Scope scope) {
		assert (scope != null);
		return _scopeSet.remove(scope);
	}

	/**
	 * Close a specific scope.
	 *
	 * @param the
	 *            scope to be closed
	 * @return true if the scope is successfully closed. false otherwise.
	 *
	 */
	@Override
	public boolean closeScope(Scope scope) {
		return removeScope(scope);
	}

	/**
	 * Checks whether a specific scope has been opened. Note that the scope can
	 * be under-specified with respect to the matching element in the set.
	 *
	 * @param the
	 *            (possibly under-specified) scope to be found
	 * @return true if the scope is in the set. false otherwise.
	 *
	 */
	@Override
	public boolean isScopeOpened(Scope scope) {
		return _scopeSet.contains(scope);
	}

	/**
	 * Returns the only element that should match the (possibly under-specified)
	 * scope in the set of currently opened scopes. Note that if more than one
	 * active (i.e. opened but not closed) scope matches the parameter, the
	 * method returns null. Similarly, if no scope is found, the method returns
	 * null.
	 *
	 * There must exists only one matching otherwise the information about the
	 * scope are not enough to identify to which scope a certain event belongs
	 *
	 * @param the
	 *            (possibly under-specified) scope to be found
	 * @return the opened scope, if found. null if more than one match or no
	 *         match is found.
	 *
	 */
	@Override
	public Scope getOpenedScope(Scope scope) {
		// if at least one matching exists...
		if (isScopeOpened(scope)) {

			// ...then clone the set...
			Set<Scope> tmpSet = new HashSet<>();
			tmpSet.addAll(_scopeSet);

			// ...remove it...
			tmpSet.remove(scope);

			// ..and check whether another one exists. If that is the case
			// return null...
			if (tmpSet.contains(scope))
				return null;

			// ..otherwise return the only matching from the original set.
			for (Scope s : _scopeSet)
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


	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");

		sb.append("  Scope:" + nl);
		sb.append("+++ NOT IMPLEMENTED +++");

		return sb.toString();
	}
}

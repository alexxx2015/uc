package de.tum.in.i22.uc.cm.interfaces.informationFlowModel;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;


public interface IScopeInformationFlowModel {

	public abstract String toString();

	public abstract void reset();

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	public abstract void push();

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 * @return true if the state has been successfully restored, false otherwise
	 */
	public abstract void pop();

	/**
	 * Adds a new scope to the set.
	 *
	 * @param scope
	 * @return true if the scope is not already present in the set. false
	 *         otherwise.
	 *
	 */
	public abstract boolean addScope(IScope scope);

	/**
	 * opens a new scope.
	 *
	 * @param the
	 *            new scope to open
	 * @return true if the scope is not already opened. false otherwise.
	 *
	 */
	public abstract boolean openScope(IScope scope);

	/**
	 * Removes a scope from the set.
	 *
	 * @param scope
	 * @return true if the scope is successfully removed. false otherwise.
	 *
	 */
	public abstract boolean removeScope(IScope scope);

	/**
	 * Close a specific scope.
	 *
	 * @param the
	 *            scope to be closed
	 * @return true if the scope is successfully closed. false otherwise.
	 *
	 */
	public abstract boolean closeScope(IScope scope);

	/**
	 * Checks whether a specific scope has been opened. Note that the scope can
	 * be under-specified with respect to the matching element in the set.
	 *
	 * @param the
	 *            (possibly under-specified) scope to be found
	 * @return true if the scope is in the set. false otherwise.
	 *
	 */
	public abstract boolean isScopeOpened(IScope scope);

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
	public abstract IScope getOpenedScope(IScope scope);

	public abstract String niceString();

}
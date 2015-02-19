package de.tum.in.i22.uc.cm.datatypes.interfaces;

import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;

public interface IScope {

	/**
	 * NOTE: Thanks to Florian Kelbert and to his attempts to make MY code look
	 * better without bothering to read the comments, now the code of the equals
	 * behaves exactly like any other equals, meaning that two scopes are the
	 * same if they are of the same type and if the attributes maps are the same.
	 * 
	 * 
	 * Two scopes are the same if they have the same type and the same list of
	 * attributes. the field _id is unique for each scope so it cannot match.
	 * _humanreadableName is also just for debugging purposes so it is not
	 * considered in the comparison.
	 *
	 * Note that o may contain additional attributes. The correct interpretation
	 * of this equality function is more that of a refinement relations.
	 *
	 * This also implies that it is not reflexive.
	 *
	 * a.equals(b) is not necessarily the same as b.equals(a)
	 *
	 * a.equals(b) is true if every attribute in a is also in b, i.e. if b
	 * refines a.
	 *
	 *
	 *
	 *
	 *
	 * @param o
	 *            the scope to compare to
	 * @return true if they are the same. false otherwise.
	 */
	boolean equals(Object obj);

	int hashCode();

	String toString();

	/**
	 * @return the _humanReadableName
	 */
	String getHumanReadableName();

	/**
	 * @return the unique id
	 */
	String getId();

	/**
	 * @return the scope type
	 */
	EScopeType getScopeType();

	/**
	 * @return the value of an attribute with key <code>key</key>
	 */
	Object getAttribute(String key);
	
	/**
	 * Replacing the old equals method that was actually a refine method
	 * @param s
	 * @return
	 */

	boolean isRefinedBy(IScope s);

}
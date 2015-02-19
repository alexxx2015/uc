package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;

/**
 * @author lovat
 *
 */
public class ScopeBasic implements IScope {
	// Define a class of scopes.
	// This is used as a simple filter to rule out certain scopes when searching
	// for a specific one

	private final String _id;
	private final String _humanReadableName;
	private final Map<String, Object> _attributes;
	private final EScopeType _scopeType;

	public ScopeBasic() {
		this("<empty scope>", EScopeType.UNKNOWN, Collections.<String, Object> emptyMap());
	}

	public ScopeBasic(String humanReadableName, EScopeType st, Map<String, Object> attributes) {
		_id = UUID.randomUUID().toString();
		_humanReadableName = humanReadableName;
		_attributes = attributes;
		_scopeType = st;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScopeBasic) {
			ScopeBasic o = (ScopeBasic) obj;
			return Objects.equals(_scopeType, o._scopeType) && Objects.equals(_attributes, o._attributes);
		}
		return false;
	}

	@Override
	public boolean isRefinedBy(IScope s) {
		if ((s==null)||(s.getScopeType()==null ) || (!(s.getScopeType().equals(this._scopeType)))) return false; 
		for (Entry<String, Object> entry : _attributes.entrySet()) {
			Object o1 = entry.getValue();
			Object o2 = s.getAttribute(entry.getKey());
			if (!o1.equals(o2))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_scopeType, _attributes);
	}

	@Override
	public String toString() {
		return "[(" + _id + ") " + _humanReadableName +
		// ", _attributes=" + _attributes
		// + ", _scopeType=" + _scopeType +
				"]";
	}

	/**
	 * @return the _humanReadableName
	 */
	public String getHumanReadableName() {
		return _humanReadableName;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public EScopeType getScopeType() {
		return _scopeType;
	}

	public Object getAttribute(String key) {
		return _attributes.get(key);
	}

}

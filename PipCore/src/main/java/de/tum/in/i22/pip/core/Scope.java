package de.tum.in.i22.pip.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lovat
 * 
 */
public class Scope {
	// Define a class of scopes.
	// This is used as a simple filter to rule out certain scopes when searching
	// for a specific one
	public enum scopeType {
		EMPTY, SAVE_FILE, LOAD_FILE, COPY_CLIPBOARD, PASTE_CLIPBOARD, SEND_SOCKET, READ_SOCKET, SEND_EMAIL, GET_EMAIL, GENERIC_IN, GENERIC_OUT
	}

	private String _id;
	private String _humanReadableName;
	private Map<String, Object> _attributes;
	private scopeType _scopeType;
	
	private String getNewRandomId() {
		return UUID.randomUUID().toString();
	}

	public Scope() {
		_id = getNewRandomId();
		_humanReadableName = "<empty scope>";
		_attributes = new HashMap<>();
		_scopeType = scopeType.EMPTY;
	}

	public Scope(String humanReadableName, scopeType st, Map<String, Object> attributes) {
		_id = getNewRandomId();
		_humanReadableName = humanReadableName;
		_attributes = attributes;
		_scopeType=st;
	}
	
	
	/**
	 * Two scopes are the same if they have the same type and the same list of attributes.
	 * the field _id is unique for each scope so it cannot match. _humanreadableName is
	 * also just for debugging purposes so it is not considered in the comparison.
	 * 
	 * Note that o may contain additional attributes.
	 * The correct interpretation of this equality function is more that of a refinement relations.
	 * 
	 *  This also implies that it is not reflexive.
	 *  
	 *  a.equals(b) is not necessarily the same as b.equals(a)
	 *  
	 *  a.equals(b) is true if every attribute in a is also in b, i.e. if b refines a.
	 *    
	 * 
	 * @param o the scope to compare to
	 * @return true if they are the same. false otherwise.
	 */
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Scope)) {
			return false;
		}
		Scope otherScope = (Scope) o;

		if (_scopeType!=otherScope.get_scopeType()){
			return false;
		}
		
		for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			Object otherVal = otherScope.get_attributes().get(key);
			if (otherVal == null) {
				return false;
			} else {
				if (!(otherVal.equals(val))) {
					return false;
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Scope [_id=" + _id + ", _humanReadableName="
				+ _humanReadableName + ", _attributes=" + _attributes
				+ ", _scopeType=" + _scopeType + "]";
	}

	/**
	 * A new hash function is required to re-define the equal
	 * 
	 * @return hash
	 */
	public int hashCode() {
		int result = 17 * 31;
//		for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
//			String key = entry.getKey();
//			Object val = entry.getValue();
//			result+=key.toString().hashCode()*41 + val.toString().hashCode()*23; 
//		}
		return result;
	}

	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id
	 *            the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * @return the _humanReadableName
	 */
	public String get_humanReadableName() {
		return _humanReadableName;
	}

	/**
	 * @param _humanReadableName
	 *            the _humanReadableName to set
	 */
	public void set_humanReadableName(String _humanReadableName) {
		this._humanReadableName = _humanReadableName;
	}

	/**
	 * @return the _attributes
	 */
	public Map<String, Object> get_attributes() {
		return _attributes;
	}

	/**
	 * @param _attributes
	 *            the _attributes to set
	 */
	public void set_attributes(Map<String, Object> _attributes) {
		this._attributes = _attributes;
	}

	/**
	 * @return the _scopeType
	 */
	public scopeType get_scopeType() {
		return _scopeType;
	}

	/**
	 * @param _scopeType the _scopeType to set
	 */
	public void set_scopeType(scopeType _scopeType) {
		this._scopeType = _scopeType;
	}

}

package de.tum.in.i22.uc.cm.basic;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStateEventMap;

public class StateEventMapBasic
	implements IStateEventMap {

	private static final Logger _logger = LoggerFactory.getLogger(StateEventMapBasic.class);
	private final Map<String, IEvent> _map;

	public StateEventMapBasic(Map<String, IEvent> map) {
		super();
		_map = map;
	}


	@Override
	public Map<String, IEvent> getMap() {
		return _map;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof StateEventMapBasic) {
			isEqual = Objects.equals(_map, ((StateEventMapBasic)obj)._map);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_map);
	}

}

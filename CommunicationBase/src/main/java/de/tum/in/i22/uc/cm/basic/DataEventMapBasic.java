package de.tum.in.i22.uc.cm.basic;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IDataEventMap;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public class DataEventMapBasic implements IDataEventMap {
	private static final Logger _logger = LoggerFactory.getLogger(DataEventMapBasic.class);

	private final Map<IData, IEvent> _map;

	public DataEventMapBasic(Map<IData, IEvent> map) {
		super();
		_map = map;
	}


	@Override
	public Map<IData, IEvent> getMap() {
		return _map;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof DataEventMapBasic) {
			isEqual = Objects.equals(_map, ((DataEventMapBasic) obj)._map);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_map);
	}
}

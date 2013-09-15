package de.tum.in.i22.pip.core.manager;

import java.util.Map;

public class PipClassLoader extends ClassLoader {
	private Map<String, byte[]> _map = null;
	
	public PipClassLoader(ClassLoader parent, Map<String, byte[]> map) {
		super(parent);
		this._map = map;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!_map.containsKey(name)) {
			return super.loadClass(name);
		}
		
		return defineClass(name, _map.get(name), 0, _map.get(name).length);
	}
}

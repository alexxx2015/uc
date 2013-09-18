package de.tum.in.i22.pip.core.manager;

import org.apache.log4j.Logger;


public class PipClassLoader extends ClassLoader {
	
	private static final Logger _logger = Logger
			.getLogger(PipClassLoader.class);
	
	private String _nameOfTheClassToLoad;
	private byte[] _classBytes;
	private boolean _classLoaded;
	
	public PipClassLoader(ClassLoader parent, String nameOfTheClassToLoad, byte[] classBytes) {
		super(parent);
		_nameOfTheClassToLoad = nameOfTheClassToLoad;
		_classBytes = classBytes;
		_classLoaded = false;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!_nameOfTheClassToLoad.equals(name) || _classLoaded) {
			return super.loadClass(name);
		}
		
		_logger.trace("Define class " + name + " from bytes");
		Class<?> result = defineClass(name, _classBytes, 0, _classBytes.length);
		_classLoaded = true;
		return result;
	}
}

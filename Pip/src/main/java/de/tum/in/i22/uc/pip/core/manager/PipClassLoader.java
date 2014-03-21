package de.tum.in.i22.uc.pip.core.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PipClassLoader extends ClassLoader {

	private static final Logger _logger = LoggerFactory
			.getLogger(PipClassLoader.class);

	private final String _nameOfTheClassToLoad;
	private final byte[] _classBytes;
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
